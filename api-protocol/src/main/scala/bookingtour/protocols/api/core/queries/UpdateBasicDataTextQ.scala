package bookingtour.protocols.api.core.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateBasicDataTextQ(
    id: UUID,
    dataId: UUID,
    text: String
)

object UpdateBasicDataTextQ {
  implicit final val updateBasicDataTextQEnc: Encoder[UpdateBasicDataTextQ] = deriveEncoder
  implicit final val updateBasicDataTextQDec: Decoder[UpdateBasicDataTextQ] = deriveDecoder

  implicit final val updateBasicDataTextQO: Order[UpdateBasicDataTextQ] =
    (x: UpdateBasicDataTextQ, y: UpdateBasicDataTextQ) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.dataId.compareTo(y.dataId),
        x.text.compareTo(y.text)
      )
}
