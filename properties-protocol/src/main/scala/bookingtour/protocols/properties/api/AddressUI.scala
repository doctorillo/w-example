package bookingtour.protocols.properties.api

import java.util.UUID

import bookingtour.protocols.doobie.values.parties.GPoint
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class AddressUI(
  id: UUID,
  cityId: UUID,
  districtId: Option[UUID],
  partyId: UUID,
  street: String,
  internal: Option[String],
  location: Option[String],
  point: Option[GPoint]
)

object AddressUI {
  type Id = UUID

  implicit final val addressUIR: AddressUI => Id = _.id
}
