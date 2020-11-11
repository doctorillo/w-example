package bookingtour.protocols.parties.api

import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.LabelId
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.parties.newTypes.CityDistrictId
import bookingtour.protocols.parties.newTypes.CityDistrictId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CityDistrictUI(id: CityDistrictId, cityId: UUID, label: LabelAPI)

object CityDistrictUI {
  type Id = LabelId

  implicit final val cityDistrictUIR: CityDistrictUI => Id = _.label.id
}
