package bookingtour.protocols.parties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.LabelId
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.parties.newTypes.{CityId, RegionId}
import bookingtour.protocols.parties.newTypes.{CityId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CityUI(id: CityId, regionId: RegionId, label: LabelAPI)

object CityUI {
  type Id = LabelId

  implicit final val cityUIR: CityUI => Id = _.label.id

  implicit final val cityUIPart: CityUI => RegionId = _.regionId
}
