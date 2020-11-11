package bookingtour.protocols.parties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.LabelId
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.parties.newTypes.CountryId
import bookingtour.protocols.parties.newTypes.CountryId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CountryUI(id: CountryId, label: LabelAPI)

object CountryUI {
  type Id = LabelId

  implicit final val countryUIR: CountryUI => Id = _.label.id

  implicit final val countryUIPart: CountryUI => Int = _ => 0
}
