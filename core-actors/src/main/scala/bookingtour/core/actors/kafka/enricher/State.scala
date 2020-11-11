package bookingtour.core.actors.kafka.enricher

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.core.doobie.env.{TakeAnswerLayer, TakeByAnswerLayer}
import bookingtour.protocols.actors.channels.MakeChannel
import bookingtour.protocols.core.actors.internal.PendingId
import bookingtour.protocols.core.actors.internal.PendingId.{PendingDeleteId, PendingTruncateId, PendingUpsertId}
import bookingtour.protocols.core.actors.operations.OpCommand.Start
import bookingtour.protocols.core.db.DbEventPayload
import bookingtour.protocols.core.db.enumeration.DbEvent
import bookingtour.protocols.core.messages.TaggedChannel
import bookingtour.protocols.core.register.RegisterEntity.Aux
import cats.Order
import cats.instances.tuple._
import zio.Managed

/**
  * © Alexey Toroshchin 2019.
  */
protected[enricher] trait State[Value, DbId, Id, Stamp] {
  _: Actor with Timers with ActorLogging =>

  final type Deleted  = List[Id]
  final type Upserted = List[Id]
  final type Stamped  = Option[Stamp]
  final type Grouped  = (Deleted, Upserted, Stamped)

  val pool: EdgeRef
  val uniqueTag: String
  val table: String
  val topic: String
  val truncateEntity: Aux[DbEventPayload.TruncateEntity[Stamp]]
  val updateEntity: Aux[DbEventPayload.BaseEntity[DbId, Stamp]]
  val attemptStamp: List[Value] => Stamp
  val readAll: Managed[Throwable, TakeAnswerLayer.Service[Value]]
  val readUpdated: Managed[Throwable, TakeByAnswerLayer.Service[DbId, Value]]
  val channelFactory: MakeChannel[Value, Id]
  val dropBefore: Instant
  val batchSize: Int
  val batchWindowMs: Int
  val enableTrace: Boolean

  implicit val zioRuntime: zio.Runtime[zio.ZEnv]
  val toId: DbId => Id
  val fromId: Id => DbId
  implicit val itemO: Order[Value]
  implicit val idReader: Value => Id
  implicit val idO: Order[Id]
  implicit val stampO: Order[Stamp]

  implicit final val tupleO: Ordering[(Stamp, DbEvent, Id)] =
    implicitly[Order[(Stamp, DbEvent, Id)]].toOrdering

  protected final val channelStateId: UUID = UUID.randomUUID()

  private final val timerKey: String                                = UUID.randomUUID().toString
  implicit final protected val taggedChannel: Option[TaggedChannel] = None

  protected final def timerActive(): Boolean = timers.isTimerActive(timerKey)

  protected final def timerCancel(): Unit = {
    if (timerActive()) {
      timers.cancel(timerKey)
    }
  }

  protected final def onPending(pending: List[PendingId[Id]]): Unit = {
    val size = pending.foldLeft(0L) { (acc, x) =>
      x match {
        case PendingDeleteId(_, data) =>
          acc + data.length
        case PendingTruncateId(_) =>
          batchSize.toLong
        case PendingUpsertId(_, data) =>
          acc + data.length
      }
    }
    timerCancel()
    if (size >= batchSize) {
      self ! Start
    } else {
      timers.startSingleTimer(
        timerKey,
        Start,
        batchWindowMs.milliseconds
      )
    }
  }

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown.")
    }
    context.stop(self)
  }
}
