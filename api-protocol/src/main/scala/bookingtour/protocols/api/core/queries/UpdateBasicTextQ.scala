package bookingtour.protocols.api.core.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateBasicTextQ(
    id: UUID,
    text: String
)

object UpdateBasicTextQ {
  implicit final val updateBasicTextQEnc: Encoder[UpdateBasicTextQ] = deriveEncoder
  implicit final val updateBasicTextQDec: Decoder[UpdateBasicTextQ] = deriveDecoder

  implicit final val updateBasicTextQO: Order[UpdateBasicTextQ] =
    (x: UpdateBasicTextQ, y: UpdateBasicTextQ) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.text.compareTo(y.text)
      )
}
