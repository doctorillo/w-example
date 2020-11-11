package bookingtour.core.actors.primitives.upserters.batch

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash, Timers}
import bookingtour.core.doobie.modules.{BatchCreateOps, DataModule}
import bookingtour.core.doobie.queries.BatchCreateVoid
import bookingtour.core.doobie.queries.BatchCreateVoid
import bookingtour.protocols.actors.{ActorProducer, ActorUnit}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import cats.Order
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class In1BatchUpsertActor[Value, Id] private (
    val uniqueTag: String,
    val producer0: ActorProducer[Value, Id],
    val runUpsert: BatchCreateVoid[Value],
    //val runDelete: BatchDeleteOps[Id],
    val batchSize: Int,
    val enableTrace: Boolean
)(
    implicit val paramR: Value => Id,
    val paramO: Order[Value],
    val paramIdO: Order[Id]
) extends Actor with Stash with Timers with ActorLogging with State[Value, Id] with SubscribeBehavior[Value, Id]
    with BasicBehavior[Value, Id] {
  override def preStart(): Unit = {
    super.preStart()
    subscribeBehavior()
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. post-re-start. {}", reason)
  }

  def receive: Receive = Actor.emptyBehavior
}

object In1BatchUpsertActor {
  final def make[Value, Id](
      uniqueTag: String,
      producer0: ActorProducer[Value, Id],
      createOps: BatchCreateOps[Value, _],
      /*runDelete: BatchDeleteOps[Id] = new BatchDeleteOps[Id] {
      def run(in: List[Id])(cb: Either[List[Throwable], Unit] => Unit): Unit =
        cb(Right(()))
    },*/
      batchSize: Int = 2000,
      parN: Int = 1,
      enableTrace: Boolean = false
  )(
      implicit ctx: ActorSystem,
      dataModule: DataModule,
      paramR: Value => Id,
      paramO: Order[Value],
      paramIdO: Order[Id]
  ): ActorUnit =
    ctx
      .actorOf(
        Props(
          new In1BatchUpsertActor(
            uniqueTag = uniqueTag,
            producer0 = producer0,
            runUpsert = BatchCreateVoid
              .instance(createOps, parN = parN, parSize = Math.round(batchSize / parN)),
            //runDelete = runDelete,
            batchSize = batchSize,
            enableTrace = enableTrace
          )
        )
      )
      .coerce[ActorUnit]

  final def registerSignalConsumer(upsert: ActorUnit, consumer: ActorRef, tag: String): Unit =
    upsert.x.tell(
      msg = SignalChannelCreate(channelId = UUID.randomUUID(), tag = tag, consumer = consumer),
      sender = consumer
    )
}
