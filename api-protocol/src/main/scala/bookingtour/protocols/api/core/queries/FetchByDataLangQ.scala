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
final case class FetchByDataLangQ(dataId: UUID, lang: LangItem)

object FetchByDataLangQ {
  implicit final val fetchByDataLangQEnc: Encoder[FetchByDataLangQ] = deriveEncoder
  implicit final val fetchByDataLangQDec: Decoder[FetchByDataLangQ] = deriveDecoder

  implicit final val fetchByDataLangQO: Order[FetchByDataLangQ] =
    (x: FetchByDataLangQ, y: FetchByDataLangQ) =>
      CompareOps.compareFn(
        x.dataId.compareTo(y.dataId),
        x.lang.compare(y.lang)
      )
}
