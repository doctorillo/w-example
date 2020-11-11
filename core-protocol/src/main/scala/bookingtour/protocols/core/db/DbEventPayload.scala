package bookingtour.protocols.core.db

import bookingtour.protocols.core.db.enumeration.DbEvent
import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DbEventPayload[A](
    val db: String,
    val table: String,
    val event: DbEvent,
    val stamp: A
) extends Product with Serializable

object DbEventPayload {
  final case class TruncateEntity[A](
      override val db: String,
      override val table: String,
      override val stamp: A
  ) extends DbEventPayload(db, table, DbEvent.Truncate, stamp)

  final case class BaseEntity[A, B](
      override val db: String,
      override val table: String,
      id: A,
      override val event: DbEvent,
      override val stamp: B
  ) extends DbEventPayload(db, table, event, stamp)

  implicit final def truncateEntityEnc[A: Encoder]: Encoder[TruncateEntity[A]] =
    deriveEncoder
  implicit final def truncateEntityDec[A: Decoder]: Decoder[TruncateEntity[A]] =
    deriveDecoder

  implicit final def baseEntityEnc[A: Encoder, B: Encoder]: Encoder[BaseEntity[A, B]] =
    deriveEncoder

  implicit final def baseEntityDec[A: Decoder, B: Decoder]: Decoder[BaseEntity[A, B]] =
    deriveDecoder

  implicit final def entityEnc[A: Encoder, B: Encoder]: Encoder[DbEventPayload[B]] =
    Encoder.instance {
      case x @ TruncateEntity(_, _, _) =>
        x.asJson
      case x @ BaseEntity(_, _, _, _, _) =>
        x.asInstanceOf[BaseEntity[A, B]].asJson
    }

  implicit final def entityDec[A: Decoder, B: Decoder]: Decoder[DbEventPayload[B]] =
    (c: HCursor) => {
      val id = for {
        a <- c.downField("id").as[A]
      } yield a
      val item = for {
        a  <- c.downField("event").as[DbEvent]
        b  <- c.downField("db").as[String]
        _c <- c.downField("table").as[String]
        d  <- c.downField("stamp").as[B]
      } yield (a, b, _c, d)
      item match {
        case Left(thr) =>
          Left(thr)
        case Right((dbevent, db, table, stamp)) =>
          dbevent match {
            case DbEvent.Truncate =>
              Right(TruncateEntity(db, table, stamp))
            case _ =>
              id match {
                case Right(_id) =>
                  Right(BaseEntity(db, table, _id, dbevent, stamp))

                case Left(thr) =>
                  Left(thr)
              }
          }
      }
    }

  implicit final def truncateO[A: Order]: Order[TruncateEntity[A]] =
    (x: TruncateEntity[A], y: TruncateEntity[A]) =>
      CompareOps.compareFn(
        x.db.compareTo(y.db),
        x.table.compareTo(y.table),
        x.event.compare(y.event),
        x.stamp.compare(y.stamp)
      )

  implicit final def baseEntityO[A: Order, B: Order]: Order[BaseEntity[A, B]] =
    (x: BaseEntity[A, B], y: BaseEntity[A, B]) =>
      CompareOps.compareFn(
        x.id.compare(y.id),
        x.db.compareTo(y.db),
        x.table.compareTo(y.table),
        x.event.compare(y.event),
        x.stamp.compare(y.stamp)
      )
}
