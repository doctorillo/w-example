package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{ Pax, PaxOnExtraBed, PaxOnMain }
import bookingtour.protocols.core.values.api.{ DescriptionAPI, LabelAPI }
import bookingtour.protocols.properties.newTypes._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class RoomUnitAPI(
  id: RoomUnitId,
  propertyId: PropertyId,
  typeId: RoomTypeId,
  typeLabels: List[LabelAPI],
  categoryId: RoomCategoryId,
  categoryLabels: List[LabelAPI],
  onMain: PaxOnMain,
  onExb: PaxOnExtraBed,
  descriptions: List[DescriptionAPI]
) {
  def pax: Pax = Pax(onMain.x + onExb.x)
}

object RoomUnitAPI {
  type Id = RoomUnitId

  implicit final val itemR0: RoomUnitAPI => Id = _.id

  implicit final val itemP0: RoomUnitAPI => (
    PropertyId,
    RoomUnitAPI
  ) = x => (x.propertyId, x)
}
