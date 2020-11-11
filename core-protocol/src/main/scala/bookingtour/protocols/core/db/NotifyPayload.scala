package bookingtour.protocols.core.db

import java.time.{Instant, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core.db.DbEventPayload._
import bookingtour.protocols.core.db.enumeration.{DbEvent, PgDbEvent}
import io.circe.derivation._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class NotifyPayload(
    val table: String,
    val action: PgDbEvent,
    val updated: LocalDateTime
) extends Product with Serializable {
  def toEvent(db: String): DbEventPayload[Instant]
}

object NotifyPayload {
  final case class TruncateItem(
      override val table: String,
      override val updated: LocalDateTime
  ) extends NotifyPayload(table, PgDbEvent.Truncate, updated) {
    override def toEvent(db: String): DbEventPayload[Instant] =
      TruncateEntity(db, table, updated.toInstant(ZoneOffset.UTC))
  }

  final object TruncateItem {
    implicit val truncateItemEnc: Encoder[TruncateItem] = deriveEncoder
    implicit val truncateItemDec: Decoder[TruncateItem] = deriveDecoder
  }

  final case class BaseItem[A](
      override val table: String,
      id: A,
      override val action: PgDbEvent,
      override val updated: LocalDateTime
  ) extends NotifyPayload(table, action, updated) {
    override def toEvent(db: String): DbEventPayload[Instant] =
      BaseEntity(db, table, id, DbEvent.fromPG(action), updated.toInstant(ZoneOffset.UTC))
  }

  final object BaseItem {
    implicit def baseItemEnc[A: Encoder]: Encoder[BaseItem[A]] = deriveEncoder

    implicit def baseItemDec[A: Decoder]: Decoder[BaseItem[A]] = deriveDecoder
  }

  implicit final def notifyPayloadEnc[A: Encoder]: Encoder[NotifyPayload] =
    Encoder.instance {
      case x @ TruncateItem(_, _) =>
        x.asJson
      case x: BaseItem[_] =>
        x.asInstanceOf[BaseItem[A]].asJson
    }

  implicit final def notifyPayloadDec[A: Decoder]: Decoder[NotifyPayload] =
    (c: HCursor) => {
      (for {
        a <- c.downField("id").as[A]
      } yield a) match {
        case Left(_) =>
          for {
            a <- c.downField("table").as[String]
            b <- c.downField("updated").as[LocalDateTime]
          } yield TruncateItem(a, b)

        case Right(_) =>
          c.as[BaseItem[A]]
      }
    }
}
