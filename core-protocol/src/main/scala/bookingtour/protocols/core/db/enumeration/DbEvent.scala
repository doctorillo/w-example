package bookingtour.protocols.core.db.enumeration

import scala.collection.immutable

import bookingtour.protocols.core.messages.MessageBodyType
import cats.Order
import enumeratum.values.{StringEnum, StringEnumEntry}
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DbEvent(val value: String, val order: Int) extends StringEnumEntry

case object DbEvent extends StringEnum[DbEvent] {
  override def values: immutable.IndexedSeq[DbEvent] = findValues

  case object Truncate extends DbEvent(value = "TRUNCATE", order = 0)

  case object Delete extends DbEvent(value = "DELETE", order = 1)

  case object Update extends DbEvent(value = "UPDATE", order = 2)

  case object Insert extends DbEvent(value = "INSERT", order = 3)

  case object Unknown extends DbEvent(value = "UNKNOWN", order = 4)

  implicit final val dbEventEnc: Encoder[DbEvent] =
    Encoder.instance(_.value.asJson)
  implicit final val dbEventDec: Decoder[DbEvent] = (c: HCursor) => c.as[String].map(DbEvent.withValue)

  implicit final val dbEventO: Order[DbEvent] = (x: DbEvent, y: DbEvent) => x.order.compare(y.order)

  final def fromPG(event: PgDbEvent): DbEvent = event match {
    case PgDbEvent.Truncate =>
      Truncate
    case PgDbEvent.Delete =>
      Delete
    case PgDbEvent.Update =>
      Update
    case PgDbEvent.Insert =>
      Insert
    case PgDbEvent.Unknown =>
      Unknown
  }

  final def fromMSSQL(event: MssqlChangeOperation): DbEvent = event match {
    case MssqlChangeOperation.Delete =>
      Delete
    case MssqlChangeOperation.Update =>
      Update
    case MssqlChangeOperation.Insert =>
      Insert
    case MssqlChangeOperation.Unknown =>
      Unknown
  }

  implicit final class DbEventOps(private val value: DbEvent) extends AnyVal {
    def toBodyEventType: MessageBodyType = value match {
      case DbEvent.Insert =>
        MessageBodyType.Created
      case DbEvent.Update =>
        MessageBodyType.Updated
      case DbEvent.Delete =>
        MessageBodyType.Deleted
      case DbEvent.Truncate =>
        MessageBodyType.Deleted
      case DbEvent.Unknown =>
        MessageBodyType.InterChange
    }

    def toPG: PgDbEvent = value match {
      case Truncate =>
        PgDbEvent.Truncate
      case Delete =>
        PgDbEvent.Delete
      case Update =>
        PgDbEvent.Update
      case Insert =>
        PgDbEvent.Insert
      case Unknown =>
        PgDbEvent.Unknown
    }

    def toMSSQL: MssqlChangeOperation = value match {
      case Truncate =>
        MssqlChangeOperation.Delete
      case Delete =>
        MssqlChangeOperation.Delete
      case Update =>
        MssqlChangeOperation.Update
      case Insert =>
        MssqlChangeOperation.Insert
      case Unknown =>
        MssqlChangeOperation.Unknown
    }
  }
}
