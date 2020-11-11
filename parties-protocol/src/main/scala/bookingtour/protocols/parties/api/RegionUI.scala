package bookingtour.protocols.parties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.LabelId
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.parties.newTypes.{CountryId, RegionId}
import bookingtour.protocols.parties.newTypes.{CountryId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RegionUI(id: RegionId, countryId: CountryId, label: LabelAPI)

object RegionUI {
  type Id = LabelId

  implicit final val regionUIR: RegionUI => Id = _.label.id

  implicit final val regionUIPart: RegionUI => CountryId = _.countryId
}
