package bookingtour.protocols.api.core.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import cats.instances.option._
import cats.instances.uuid._
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateLabelQ(
    id: UUID,
    parentId: Option[UUID],
    dataId: UUID,
    lang: LangItem,
    label: String
)

object UpdateLabelQ {
  implicit final val updateLabelQEnc: Encoder[UpdateLabelQ] = deriveEncoder
  implicit final val updateLabelQDec: Decoder[UpdateLabelQ] = deriveDecoder

  implicit final val updateLabelQO: Order[UpdateLabelQ] =
    (x: UpdateLabelQ, y: UpdateLabelQ) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.parentId.compare(y.parentId),
        x.dataId.compareTo(y.dataId),
        x.lang.compare(y.lang),
        x.label.compareTo(y.label)
      )
}
