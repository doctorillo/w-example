package bookingtour.protocols.doobie.values.properties.interlook

import java.time.LocalDate

import bookingtour.protocols.core.values.enumeration.QuotaItem

/**
  * © Alexey Toroshchin 2019.
  */
final case class QuotaDetail(
    id: Int,
    propertyId: Int,
    date: LocalDate,
    quotaType: QuotaItem,
    places: Int,
    release: Int
)

object QuotaDetail {
  type Id = Int
  import bookingtour.protocols.core.types.CompareOps
  import cats.Order
  import cats.syntax.order._
  import io.circe.derivation._
  import io.circe.{Decoder, Encoder}

  implicit val quotaDetailEnc: Encoder[QuotaDetail] = deriveEncoder
  implicit val quotaDetailDec: Decoder[QuotaDetail] = deriveDecoder

  implicit final val quotaDetailO: Order[QuotaDetail] = (x: QuotaDetail, y: QuotaDetail) =>
    CompareOps.compareFn(
      x.id.compare(y.id),
      x.propertyId.compare(y.propertyId),
      x.date.compareTo(y.date),
      x.quotaType.compare(y.quotaType),
      x.places.compare(y.places),
      x.release.compare(y.release)
    )

  implicit final val quotaDetailR: QuotaDetail => Int = _.id
}
