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
final case class UpdateEnumLabelQ(
    id: UUID,
    value: Int,
    dataId: UUID,
    lang: LangItem,
    label: String
)

object UpdateEnumLabelQ {
  implicit final val updateEnumLabelQEnc: Encoder[UpdateEnumLabelQ] = deriveEncoder
  implicit final val updateEnumLabelQDec: Decoder[UpdateEnumLabelQ] = deriveDecoder

  implicit final val updateEnumLabelQO: Order[UpdateEnumLabelQ] =
    (x: UpdateEnumLabelQ, y: UpdateEnumLabelQ) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.value.compareTo(y.value),
        x.dataId.compareTo(y.dataId),
        x.lang.compare(y.lang),
        x.label.compareTo(y.label)
      )
}
