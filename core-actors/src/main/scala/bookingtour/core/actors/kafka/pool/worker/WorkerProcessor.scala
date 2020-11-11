package bookingtour.core.actors.kafka.pool.worker

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Terminated}
import akka.event.LoggingAdapter
import bookingtour.core.actors.kafka.pool.worker.KafkaConsumerWorker.EnvelopedMessage
import bookingtour.protocols.core.actors.kafka.EdgeCommand.{
  EdgeConsumerChannelCreate,
  EdgeConsumerChannelDelete,
  EdgeConsumerCreateWrapper
}
import bookingtour.protocols.core.actors.kafka.EdgeEvent._
import bookingtour.protocols.core.messages.MessageEnvelope
import bookingtour.protocols.core.messages.MessageEnvelope.{EnvelopeChannel, EnvelopeSimple}
import bookingtour.protocols.core.register.RegisterEntity
import cats.data.Chain
import cats.instances.uuid._
import cats.syntax.order._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
final class WorkerProcessor[Value] private (
    uniqueTag: String,
    enableTrace: Boolean
)(implicit runtime: zio.Runtime[zio.ZEnv], entity: RegisterEntity.Aux[Value])
    extends Actor with ActorLogging {
  import WorkerProcessor._

  private[this] def behaviors(
      consumers: Chain[EdgeConsumerChannelCreate[_ <: Any]]
  ): Receive = {
    case EnvelopedMessage(envelope, body) =>
      val tag = s"$uniqueTag. enveloped-message received."
      val consumerEffect = envelope match {
        case e: EnvelopeSimple =>
          ZIO.effectTotal(
            consumers.filter(x =>
              x.dropBefore
                .isBefore(e.expiredAt) && x.register.key === e.bodyKey
            )
          )

        case e: EnvelopeChannel =>
          ZIO.effectTotal(
            consumers.filter(x =>
              x.dropBefore
                .isBefore(e.expiredAt) && x.register.key === envelope.bodyKey && x.taggedChannel
                .exists(_.sameChannel(e.channel))
            )
          )
      }
      val effect = for {
        cxs <- consumerEffect
        _   <- ZIO.effectTotal(log.info(s"$tag consumers: ${cxs.length}.")).when(enableTrace)
        b   <- entity.decode(body)
        c <- ZIO
              .collectAll(cxs.toList.map { (d: EdgeConsumerChannelCreate[_]) =>
                val z = d.asInstanceOf[EdgeConsumerChannelCreate[Any]]
                onMessage(
                  processor = self,
                  envelope = envelope,
                  msg = b,
                  channelId = z.id,
                  filter = z.filter,
                  consumer = d.replayTo,
                  consumerUniqueTag = d.uniqueTag,
                  log = log,
                  enableTrace = enableTrace
                )
              })
              .unit
      } yield c
      runtime.unsafeRunAsync(effect) {
        case zio.Exit.Failure(cause) =>
          cause.failures
            .foreach(log.error(_))

        case zio.Exit.Success(_) =>
      }

    case wrapper: EdgeConsumerCreateWrapper[_] =>
      val tag =
        s"$uniqueTag. edge-consumer-channel-create. key: ${wrapper.msg.register.key.typeTag}. target-tag: ${wrapper.msg.taggedChannel
          .map(_.tag)
          .getOrElse(())}. unique-tag: ${wrapper.msg.uniqueTag}."
      consumers.find(_.id === wrapper.msg.id) match {
        case Some(_) =>
          log.error(s"$tag received. id exist.")
          wrapper.mediatorRef ! EdgeConsumerChannelCreated(id = wrapper.msg.id, replayTo = self)

        case None =>
          context.watch(wrapper.msg.replayTo)
          context.become(behaviors(consumers :+ wrapper.msg))
          wrapper.mediatorRef ! EdgeConsumerChannelCreated(id = wrapper.msg.id, replayTo = self)
      }

    case msg: EdgeConsumerChannelDelete =>
      val mediatorRef = sender()
      consumers.find(_.id === msg.id) match {
        case Some(consumer) =>
          context.unwatch(msg.replayTo)
          context.become(behaviors(consumers.filterNot(_.id === consumer.id)))
          mediatorRef ! EdgeConsumerChannelDeleted(id = msg.id, replayTo = self)

        case None =>
          log.error(s"${msg.uniqueTag} received. id exist.")
          mediatorRef ! EdgeChannelError(
            id = msg.id,
            error = "consumer not found.",
            replayTo = self
          )
      }

    case Terminated(actor) =>
      consumers.find(_.replayTo.equals(actor)) match {
        case Some(consumer) =>
          context.unwatch(actor)
          context.become(
            behaviors(consumers.filterNot(_.id === consumer.id))
          )

        case None =>
      }

    case msg =>
      log.error(s"$uniqueTag. receive unhandled $msg.")
      log.error(s"$uniqueTag. shutdown.")
      context.stop(self)
  }

  def receive: Receive = behaviors(Chain.empty)
}

object WorkerProcessor {
  @newtype final case class EdgeWorkerRef(x: ActorRef)

  protected[worker] final def onMessage(
      processor: ActorRef,
      envelope: MessageEnvelope,
      msg: Any,
      channelId: UUID,
      filter: Any => Boolean,
      consumer: ActorRef,
      consumerUniqueTag: String,
      log: LoggingAdapter,
      enableTrace: Boolean
  ): ZIO[Any, String, Unit] = {
    val tag = s"$consumerUniqueTag. on-message"
    for {
      a <- ZIO
            .effect(filter(msg))
            .catchAll(thr => ZIO.fail(s"$tag. filter-apply. ${thr.getMessage}."))
      _ <- ZIO.when(enableTrace)(ZIO.effectTotal(log.info(s"$tag. filter-applied. result: $a.")))
      m = envelope match {
        case e: EnvelopeSimple =>
          EdgeConsumerMessageReceived(
            id = channelId,
            envelope = e,
            msg = msg,
            replayTo = processor
          )
        case e: EnvelopeChannel =>
          EdgeChannelConsumerMessageReceived(
            id = channelId,
            envelope = e,
            msg = msg,
            replayTo = processor
          )
      }
      b <- ZIO.when(a)(
            ZIO
              .effect(consumer.tell(m, processor))
              .catchAll(thr => ZIO.fail(s"$tag. msg send to consumer. ${thr.getMessage}."))
          )
      _ <- ZIO.when(enableTrace)(
            ZIO.effectTotal(log.info(s"$tag. msg send to consumer. complete."))
          )
    } yield b
  }

  final def make[Value](
      uniqueTag: String,
      enableTrace: Boolean
  )(
      implicit ctx: ActorContext,
      runtime: zio.Runtime[zio.ZEnv],
      entity: RegisterEntity.Aux[Value]
  ): EdgeWorkerRef =
    ctx
      .actorOf(
        Props(
          new WorkerProcessor[Value](
            uniqueTag = uniqueTag,
            enableTrace = enableTrace
          )
        ),
        uniqueTag
      )
      .coerce[EdgeWorkerRef]
}
