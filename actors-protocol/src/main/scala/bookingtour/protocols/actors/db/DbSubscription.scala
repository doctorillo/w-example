package bookingtour.protocols.actors.db

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.db.enumeration.DbEvent
import cats.Order
import cats.data.Chain
import cats.instances.string._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DbSubscription extends Product with Serializable

object DbSubscription {
  sealed abstract class Command extends DbSubscription

  sealed abstract class Event extends DbSubscription

  final case class Subscribe(tables: List[String], subscriber: ActorRef) extends Command

  final case class Unsubscribe(id: UUID, subscriber: ActorRef) extends Command

  final case class SubscriptionReceived(
      id: UUID,
      tables: List[String],
      producer: ActorRef,
      subscriber: ActorRef
  ) extends Event

  sealed abstract class EventReceived[B](
      val table: String,
      val action: DbEvent,
      val stamp: B
  ) extends Event

  sealed abstract class BaseEvent[B](
      override val table: String,
      override val action: DbEvent,
      override val stamp: B
  ) extends EventReceived[B](table, action, stamp)

  final case class TruncateEventReceived[A, B](override val table: String, override val stamp: B)
      extends BaseEvent[B](table, DbEvent.Truncate, stamp)

  final case class DeleteEventReceived[A, B](
      override val table: String,
      id: A,
      override val stamp: B
  ) extends BaseEvent[B](table, DbEvent.Delete, stamp)

  final case class UpdateEventReceived[A, B](
      override val table: String,
      id: A,
      override val stamp: B
  ) extends BaseEvent[B](table, DbEvent.Update, stamp)

  sealed abstract class CompactedEvent[B](
      override val table: String,
      override val action: DbEvent,
      override val stamp: B
  ) extends EventReceived[B](table, action, stamp)

  final case class TruncateReceived[A, B](override val table: String, override val stamp: B)
      extends CompactedEvent[B](table, DbEvent.Truncate, stamp)

  final case class DeleteReceived[A, B](
      override val table: String,
      id: Chain[A],
      override val stamp: B
  ) extends CompactedEvent[B](table, DbEvent.Delete, stamp)

  final case class UpdateReceived[A, B](
      override val table: String,
      id: Chain[A],
      override val stamp: B
  ) extends CompactedEvent[B](table, DbEvent.Update, stamp)

  final object EventReceived {
    def foldMap[A, B](
        xs: List[BaseEvent[B]]
    )(implicit o: Order[B], o1: Order[A]): Chain[CompactedEvent[B]] = {
      implicit val ord: Ordering[B] = o.toOrdering
      val a                         = xs.sortBy(_.stamp).groupBy(_.action)
      val b                         = a.getOrElse(DbEvent.Insert, List.empty).asInstanceOf[List[UpdateEventReceived[A, B]]]
      val c                         = a.getOrElse(DbEvent.Update, List.empty).map(_.asInstanceOf[UpdateEventReceived[A, B]])
      val d                         = a.getOrElse(DbEvent.Delete, List.empty).asInstanceOf[List[DeleteEventReceived[A, B]]]
      val e =
        a.getOrElse(DbEvent.Truncate, List.empty).asInstanceOf[List[TruncateEventReceived[A, B]]]
      val f: Chain[TruncateReceived[A, B]] = Chain.fromSeq(e.groupBy(_.table).toList).flatMap {
        case (table, data) =>
          val maxStamp = data.map(_.stamp).max
          Chain.one(TruncateReceived[A, B](table, maxStamp))
      }
      val g: Chain[DeleteReceived[A, B]] = Chain
        .fromSeq(d.groupBy(_.table).toList)
        .flatMap {
          case (table, data) =>
            val _data    = data.filterNot(x => f.exists(y => y.table === table && y.stamp >= x.stamp))
            val maxStamp = _data.map(_.stamp).max
            val ids      = Chain.fromSeq(_data.map(_.id).distinct)
            if (ids.isEmpty) {
              Chain.empty
            } else {
              Chain.one(DeleteReceived(table, ids, maxStamp))
            }
        }
      val h = Chain
        .fromSeq((b ++ c).groupBy(_.table).toList)
        .flatMap {
          case (table, data) =>
            val _data = data
              .filterNot(x => f.exists(y => y.table === table && y.stamp >= x.stamp))
              .filterNot(dx => g.exists(x => x.id.contains(dx.id) && g.exists(x => dx.stamp <= x.stamp)))
            val maxStamp = _data.map(_.stamp).max
            val ids      = Chain.fromSeq(_data.map(_.id).distinct)
            if (ids.isEmpty) {
              Chain.empty
            } else {
              Chain.one(UpdateReceived(table, ids, maxStamp))
            }
        }
      f ++ g ++ h
    }
  }

  final case class SubscriptionEventReceived[A, B](
      sessionId: UUID,
      event: CompactedEvent[B]
  )

  /*trait ToJsonOps {
    _: DbEvent.ToJsonOps with LocalDateTimeToJsonOps =>

    implicit final val pgIntEventEnc: Encoder[PgIntEvent]       = deriveEncoder
    implicit final val pgIntEventDec: Decoder[PgIntEvent]       = deriveDecoder
    implicit final val pgUuidEventEnc: Encoder[PgUuidEvent]     = deriveEncoder
    implicit final val pgUuidEventDec: Decoder[PgUuidEvent]     = deriveDecoder
    implicit final val mssqlIntEventEnc: Encoder[MssqlIntEvent] = deriveEncoder
    implicit final val mssqlIntEventDec: Decoder[MssqlIntEvent] = deriveDecoder
  }

  final object json extends LocalDateTimeToJsonOps with DbEvent.ToJsonOps with ToJsonOps*/
}
