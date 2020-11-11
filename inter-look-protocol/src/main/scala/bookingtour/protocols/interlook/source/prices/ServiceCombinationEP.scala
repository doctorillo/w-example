package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{
  LookPartyId,
  LookRoomCategoryId,
  LookRoomTypeId,
  LookServiceCombinationId
}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ServiceCombinationEP(
    id: LookServiceCombinationId,
    property: LookPartyId,
    typeRoom: LookRoomTypeId,
    categoryRoom: LookRoomCategoryId
)

object ServiceCombinationEP {
  type Id = LookServiceCombinationId

  implicit final val itemR: ServiceCombinationEP => Id = _.id
}
