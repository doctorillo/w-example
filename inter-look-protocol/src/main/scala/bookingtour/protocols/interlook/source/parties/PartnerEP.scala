package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookCityId, LookCustomerGroupId, LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PartnerEP(
    id: LookPartyId,
    name: String,
    city: LookCityId,
    address: String,
    priceGroup: Option[LookCustomerGroupId] = None
)

object PartnerEP {
  type Id = LookPartyId

  implicit final val itemR: PartnerEP => Id = _.id

  implicit final val itemP: PartnerEP => Int = _ => 0

  final case class Output(
      id: Int,
      name: String,
      city: Int,
      address: Option[String],
      priceGroup: Option[Int]
  )

  implicit final val outputTransform: Output => PartnerEP = _.into[PartnerEP]
    .withFieldComputed(_.id, x => LookPartyId(x.id))
    .withFieldComputed(_.city, x => LookCityId(x.city))
    .withFieldComputed(_.address, x => x.address.getOrElse(x.name))
    .withFieldComputed(_.priceGroup, _.priceGroup.map(LookCustomerGroupId(_)))
    .transform
}
