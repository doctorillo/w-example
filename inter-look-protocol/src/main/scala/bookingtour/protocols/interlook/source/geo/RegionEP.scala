package bookingtour.protocols.interlook.source.geo

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookCountryId, LookRegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RegionEP(id: LookRegionId, countryId: LookCountryId, name: String)

object RegionEP {
  type Id = LookRegionId

  implicit final val itemR: RegionEP => Id = _.id

  implicit final val itemP: RegionEP => Int = _ => 0
}
