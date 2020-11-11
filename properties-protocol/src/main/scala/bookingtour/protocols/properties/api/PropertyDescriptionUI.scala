package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.values._
import bookingtour.protocols.core.values.api.ImageAPI
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyDescriptionUI(
    id: PropertyId,
    lang: LangItem,
    description: Option[PropertyDescription],
    paymentTerm: Option[PropertyPaymentTerm],
    cancellationTerm: Option[PropertyCancellationTerm],
    taxTerm: Option[PropertyTaxTerm],
    guestTerm: Option[PropertyGuestTerm],
    images: List[ImageAPI]
)

object PropertyDescriptionUI {
  type Id = PropertyId

  implicit final val itemR: PropertyDescriptionUI => Id = _.id

  implicit final val itemP: PropertyDescriptionUI => Int = _ => 0
}
