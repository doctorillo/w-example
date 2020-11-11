package bookingtour.core.actors.kafka.enricher

import java.time.Instant

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash, Timers}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.{EdgeRef, KafkaEdgeWrapper}
import bookingtour.core.doobie.env.{TakeAnswerLayer, TakeByAnswerLayer}
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.core.doobie.queries.{LiveTakeAnswer, LiveTakeByAnswer}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.channels.MakeChannel
import bookingtour.protocols.core.db.DbEventPayload
import bookingtour.protocols.core.register.RegisterEntity.Aux
import cats.Order
import doobie.hikari.HikariTransactor
import zio.{Managed, Task}

/**
  * © Alexey Toroshchin 2019.
  */
final class KafkaEnricherActor[Value, DbId, Id, Stamp](
    val pool: EdgeRef,
    val uniqueTag: String,
    val table: String,
    val topic: String,
    val readAll: Managed[Throwable, TakeAnswerLayer.Service[Value]],
    val readUpdated: Managed[Throwable, TakeByAnswerLayer.Service[DbId, Value]],
    val attemptStamp: List[Value] => Stamp,
    val channelFactory: MakeChannel[Value, Id],
    val dropBefore: Instant,
    val batchSize: Int,
    val batchWindowMs: Int,
    val enableTrace: Boolean
)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv],
    val toId: DbId => Id,
    val fromId: Id => DbId,
    val itemO: Order[Value],
    val idReader: Value => Id,
    val idO: Order[Id],
    val stampO: Order[Stamp],
    val truncateEntity: Aux[DbEventPayload.TruncateEntity[Stamp]],
    val updateEntity: Aux[DbEventPayload.BaseEntity[DbId, Stamp]]
) extends Actor with Stash with ActorLogging with Timers with State[Value, DbId, Id, Stamp]
    with LoadBehavior[Value, DbId, Id, Stamp] with SubscribeTruncateBehavior[Value, DbId, Id, Stamp]
    with SubscribeUpdateBehavior[Value, DbId, Id, Stamp] with BasicBehavior[Value, DbId, Id, Stamp] {
  override def preStart(): Unit = {
    super.preStart()
    if (enableTrace) {
      log.info(
        s"$uniqueTag. start. table: $table. topic: $topic. truncate: ${truncateEntity.key.typeTag}. update: ${updateEntity.key.typeTag}. drop-before: $dropBefore."
      )
    }
    val channel = channelFactory.make(context, self, channelStateId, s"$uniqueTag:state")
    loadBehavior(channel)
  }

  override def receive: Receive = Actor.emptyBehavior
}

object KafkaEnricherActor {
  final def make[Value, DbId, Id, Stamp](
      uniqueTag: String,
      table: String,
      topic: String,
      dataOps: GetAllOps[Value] with GetByIdListOps[DbId, Value],
      channelFactory: MakeChannel[Value, Id],
      dropBefore: Instant,
      batchSize: Int = 1000,
      batchWindowMs: Int = 250,
      enableTrace: Boolean = false
  )(
      implicit ctx: ActorSystem,
      attemptStamp: List[Value] => Stamp,
      poolWrapper: KafkaEdgeWrapper,
      zioRuntime: zio.Runtime[zio.ZEnv],
      tx: Managed[Throwable, HikariTransactor[Task]],
      truncateEntity: Aux[DbEventPayload.TruncateEntity[Stamp]],
      updateEntity: Aux[DbEventPayload.BaseEntity[DbId, Stamp]],
      toId: DbId => Id,
      fromId: Id => DbId,
      itemO: Order[Value],
      idReader: Value => Id,
      idO: Order[Id],
      stampO: Order[Stamp]
  ): ActorProducer[Value, Id] =
    ActorProducer[Value, Id](
      ctx
        .actorOf(
          Props(
            new KafkaEnricherActor(
              pool = poolWrapper.pool,
              uniqueTag = uniqueTag,
              table = table,
              topic = topic,
              readAll = LiveTakeAnswer.managed[Value](dataOps, tx),
              readUpdated = LiveTakeByAnswer.managed[DbId, Value](dataOps, tx),
              attemptStamp = attemptStamp,
              channelFactory = channelFactory,
              dropBefore = dropBefore,
              batchSize = batchSize,
              batchWindowMs = batchWindowMs,
              enableTrace = enableTrace
            )
          )
        )
    )

  final def make2[Value, DbId, Id, Stamp](
      uniqueTag: String,
      table: String,
      topic: String,
      readAll: Managed[Throwable, TakeAnswerLayer.Service[Value]],
      readUpdated: Managed[Throwable, TakeByAnswerLayer.Service[DbId, Value]],
      channelFactory: MakeChannel[Value, Id],
      dropBefore: Instant,
      batchSize: Int = 1000,
      batchWindowMs: Int = 250,
      enableTrace: Boolean = false
  )(
      implicit ctx: ActorSystem,
      attemptStamp: List[Value] => Stamp,
      poolWrapper: KafkaEdgeWrapper,
      zioRuntime: zio.Runtime[zio.ZEnv],
      truncateEntity: Aux[DbEventPayload.TruncateEntity[Stamp]],
      updateEntity: Aux[DbEventPayload.BaseEntity[DbId, Stamp]],
      toId: DbId => Id,
      fromId: Id => DbId,
      itemO: Order[Value],
      idReader: Value => Id,
      idO: Order[Id],
      stampO: Order[Stamp]
  ): ActorProducer[Value, Id] =
    ActorProducer[Value, Id](
      ctx
        .actorOf(
          Props(
            new KafkaEnricherActor(
              pool = poolWrapper.pool,
              uniqueTag = uniqueTag,
              table = table,
              topic = topic,
              readAll = readAll,
              readUpdated = readUpdated,
              attemptStamp = attemptStamp,
              channelFactory = channelFactory,
              dropBefore = dropBefore,
              batchSize = batchSize,
              batchWindowMs = batchWindowMs,
              enableTrace = enableTrace
            )
          )
        )
    )
}
