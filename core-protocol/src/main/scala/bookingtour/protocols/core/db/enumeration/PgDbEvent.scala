package bookingtour.protocols.core.db.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{StringEnum, StringEnumEntry}
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}

/**
  * Created by d0ct0r on 2019-10-22.
  */
sealed abstract class PgDbEvent(val value: String, val order: Int) extends StringEnumEntry

case object PgDbEvent extends StringEnum[PgDbEvent] {
  override def values: immutable.IndexedSeq[PgDbEvent] = findValues

  case object Truncate extends PgDbEvent(value = "TRUNCATE", order = 0)

  case object Delete extends PgDbEvent(value = "DELETE", order = 1)

  case object Update extends PgDbEvent(value = "UPDATE", order = 2)

  case object Insert extends PgDbEvent(value = "INSERT", order = 3)

  case object Unknown extends PgDbEvent(value = "UNKNOWN", order = 4)

  implicit val pgDbEventO: Order[PgDbEvent] = (x: PgDbEvent, y: PgDbEvent) => x.order.compare(y.order)

  implicit final val pgDbEventEnc: Encoder[PgDbEvent] =
    Encoder.instance(_.value.asJson)
  implicit final val pgDbEventDec: Decoder[PgDbEvent] = (c: HCursor) => c.as[String].map(PgDbEvent.withValue)

  final object syntax {
    implicit final class PgDbEventOps(private val value: PgDbEvent) extends AnyVal {
      def toDbEvent: DbEvent = DbEvent.fromPG(value)
    }
  }
}
