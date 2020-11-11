package bookingtour.protocols.api.core.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateEnumQ(id: UUID, dataId: UUID, lang: LangItem, label: String)

object UpdateEnumQ {
  implicit final val updateEnumQEnc: Encoder[UpdateEnumQ] = deriveEncoder
  implicit final val updateEnumQDec: Decoder[UpdateEnumQ] = deriveDecoder

  implicit final val updateEnumQO: Order[UpdateEnumQ] =
    (x: UpdateEnumQ, y: UpdateEnumQ) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.dataId.compareTo(y.dataId),
        x.lang.compare(y.lang),
        x.label.compareTo(y.label)
      )
}
