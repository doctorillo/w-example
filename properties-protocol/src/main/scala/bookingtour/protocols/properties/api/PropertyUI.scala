package bookingtour.protocols.properties.api

import java.util.UUID

import cats.instances.all._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class PropertyUI(
  id: UUID,
  partyId: UUID,
  editorPartyId: UUID,
  name: String,
  star: Int,
  address: AddressUI,
  hasTreatment: Boolean
)

object PropertyUI {
  type Id = UUID

  implicit final val propertyUIR: PropertyUI => Id = _.id

  implicit final val propertyUIPart: PropertyUI => (
    UUID,
    PropertyUI
  ) = x => (x.editorPartyId, x)
}
