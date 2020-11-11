package bookingtour.protocols.interlook.source.geo

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.LookCountryId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CountryEP(id: LookCountryId, name: String)

object CountryEP {
  type Id = LookCountryId

  implicit final val itemR: CountryEP => Id = _.id

  implicit final val itemP: CountryEP => Int = _ => 0
}
