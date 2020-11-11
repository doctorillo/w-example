package bookingtour.protocols.doobie.values.properties.interlook

import bookingtour.protocols.core.types.CompareOps

/**
  * © Alexey Toroshchin 2019.
  */
final case class Quota(
    id: Int,
    propertyId: Int,
    objects: List[QuotaObject],
    details: List[QuotaDetail]
)

object Quota {
  type Id = Int
  import cats.Order
  import cats.instances.list._
  import cats.syntax.order._
  import io.circe.derivation._
  import io.circe.{Decoder, Encoder}

  implicit final val quotaEnc: Encoder[Quota] = deriveEncoder
  implicit final val quotaDec: Decoder[Quota] = deriveDecoder

  implicit final val quotaO: Order[Quota] = (x: Quota, y: Quota) =>
    CompareOps.compareFn(
      x.id.compare(y.id),
      x.propertyId.compare(y.propertyId),
      x.objects.compare(y.objects),
      x.details.compare(y.details)
    )

  implicit final val quotaR: QuotaObject => Id = _.id
}
