package bookingtour.core.actors.kafka.pool.hearbeat.producer

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._

import akka.Done
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Timers}
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.actors.kafka.EdgeHeartbeat
import bookingtour.protocols.core.actors.operations.OpCommand.Start
import bookingtour.protocols.core.messages.EnvelopeRoute.PublisherOnly
import bookingtour.protocols.core.messages.MessageEnvelope.EnvelopeChannel
import bookingtour.protocols.core.messages.TaggedChannel.ChannelTag
import bookingtour.protocols.core.messages.{PostOffice, PostStamp}
import bookingtour.protocols.core.register.RegisterEntity
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class HeartbeatProducer private (
    topic: String,
    heartbeatTopic: String,
    interval: Long,
    enableTrace: Boolean
)(
    implicit r: zio.Runtime[zio.ZEnv],
    p: MessageProducer[Done],
    e: RegisterEntity.Aux[EdgeHeartbeat]
) extends Actor with Timers with ActorLogging {
  private val uniqueTag: String         = s"heartbeat-producer:$topic"
  private val timerKey: UUID            = UUID.randomUUID()
  private val taggedChannel: ChannelTag = ChannelTag(topic)
  private val postOffice: PostOffice    = PostOffice(topic)

  override def preStart(): Unit = {
    super.preStart()
    if (enableTrace) {
      log.info(s"$uniqueTag. rate: $interval seconds. start.")
    }
    timers.startTimerAtFixedRate(timerKey, Start, interval.seconds)
    self ! Start
  }

  private def makeEnvelope(): EnvelopeChannel = EnvelopeChannel(
    id = UUID.randomUUID(),
    route = PublisherOnly(heartbeatTopic),
    bodyKey = e.key,
    channel = taggedChannel,
    stamps = PostStamp.one(postOffice),
    expiredAt = Instant.now().plusSeconds(interval)
  )

  private def makeMsg(): EdgeHeartbeat = EdgeHeartbeat(topic, interval)

  def receive: Receive = {
    case Start =>
      val effect = for {
        msg <- e.encode(makeMsg())
        _ <- p.publishMessage(
              makeEnvelope(),
              message = msg
            )
      } yield ()
      r.unsafeRunAsync(effect) {
        case zio.Exit.Failure(cause) =>
          cause.failures.foreach(err => log.error(s"$uniqueTag. $err"))

        case zio.Exit.Success(_) =>
          if (enableTrace) {
            log.info(s"$uniqueTag. send heartbeat for $topic.")
          }
      }
  }
}

object HeartbeatProducer {
  @newtype final case class HeartbeatProducerRef(x: ActorRef)

  final def make(topic: String, heartbeatTopic: String, interval: Long, enableTrace: Boolean)(
      implicit ctx: ActorContext,
      r: zio.Runtime[zio.ZEnv],
      p: MessageProducer[Done],
      e: RegisterEntity.Aux[EdgeHeartbeat]
  ): HeartbeatProducerRef =
    ctx
      .actorOf(
        Props(
          new HeartbeatProducer(
            topic = topic,
            heartbeatTopic = heartbeatTopic,
            interval = interval,
            enableTrace = enableTrace
          )
        ),
        s"heartbeat-producer:$topic"
      )
      .coerce[HeartbeatProducerRef]
}
