package bookingtour.core.doobie.protocols

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.enumeration.LangItem

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateTextU(
    id: UUID,
    parentId: Option[UUID],
    dataId: UUID,
    solverId: UUID,
    lang: LangItem,
    text: String
)

object UpdateTextU {
  type Id = UUID
  import cats.Order
  import cats.instances.option._
  import cats.instances.uuid._
  import cats.syntax.order._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  implicit final val updateTextUEnc: Encoder[UpdateTextU] = deriveEncoder
  implicit final val updateTextUDec: Decoder[UpdateTextU] = deriveDecoder

  implicit final val updateTextUO: Order[UpdateTextU] = (x: UpdateTextU, y: UpdateTextU) =>
    CompareOps.compareFn(
      x.id.compareTo(y.id),
      x.parentId.compare(y.parentId),
      x.dataId.compareTo(y.dataId),
      x.solverId.compareTo(y.solverId),
      x.lang.compare(y.lang),
      x.text.compareTo(y.text)
    )

  implicit final val updateTextUR: UpdateTextU => Id = _.id
}
