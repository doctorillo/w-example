package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.parties.api.CityUI
import bookingtour.protocols.parties.newTypes.PartyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyCityUI(editorId: PartyId, city: CityUI)

object PropertyCityUI {
  type Id = PropertyCityUI
  implicit final val itemR: PropertyCityUI => Id = x => x

  implicit final val itemP: PropertyCityUI => Int = _ => 0
}
