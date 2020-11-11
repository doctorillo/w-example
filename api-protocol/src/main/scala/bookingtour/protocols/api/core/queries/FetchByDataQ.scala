package bookingtour.protocols.api.core.queries

import java.util.UUID

import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchByDataQ(dataId: UUID)

object FetchByDataQ {
  implicit final val fetchByDataQEnc: Encoder[FetchByDataQ] = deriveEncoder
  implicit final val fetchByDataQDec: Decoder[FetchByDataQ] = deriveDecoder

  implicit final val fetchByDataQO: Order[FetchByDataQ] =
    (x: FetchByDataQ, y: FetchByDataQ) => x.dataId.compareTo(y.dataId)
}
