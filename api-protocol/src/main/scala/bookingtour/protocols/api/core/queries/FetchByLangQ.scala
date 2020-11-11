package bookingtour.protocols.api.core.queries

import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchByLangQ(lang: LangItem)

object FetchByLangQ {
  implicit final val fetchByLangQEnc: Encoder[FetchByLangQ] = deriveEncoder
  implicit final val fetchByLangQDec: Decoder[FetchByLangQ] = deriveDecoder

  implicit final val fetchByLangQO: Order[FetchByLangQ] =
    (x: FetchByLangQ, y: FetchByLangQ) => x.lang.value.compareTo(y.lang.value)
}
