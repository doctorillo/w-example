package bookingtour.core.doobie.protocols

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.enumeration.LangItem

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateEnumLabelU(
    id: UUID,
    value: Int,
    dataId: UUID,
    solverId: UUID,
    lang: LangItem,
    text: String
)

object UpdateEnumLabelU {
  type Id = UUID
  import cats.Order
  import cats.syntax.order._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  implicit final val updateEnumLabelUEnc: Encoder[UpdateEnumLabelU] = deriveEncoder
  implicit final val updateEnumLabelUDec: Decoder[UpdateEnumLabelU] = deriveDecoder

  implicit final val updateEnumLabelUO: Order[UpdateEnumLabelU] =
    (x: UpdateEnumLabelU, y: UpdateEnumLabelU) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.value.compareTo(y.value),
        x.dataId.compareTo(y.dataId),
        x.solverId.compareTo(y.solverId),
        x.lang.compare(y.lang),
        x.text.compareTo(y.text)
      )

  implicit final val updateEnumLabelUR: UpdateEnumLabelU => Id = _.id
}
