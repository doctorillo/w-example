package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{
  LookAccommodationId,
  LookBoardingId,
  LookCostId,
  LookPartyId,
  LookRoomCategoryId,
  LookRoomTypeId
}
import cats.instances.all._
import cats.syntax.option._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PropertyProviderEP(
    cost: LookCostId,
    property: LookPartyId,
    supplier: LookPartyId,
    typeRoom: LookRoomTypeId,
    boarding: LookBoardingId,
    categoryRoom: LookRoomCategoryId,
    accommodation: LookAccommodationId
)

object PropertyProviderEP {
  type Id = LookCostId

  final case class Output(
      costId: Int,
      propertyId: Int,
      supplierId: Int,
      typeId: Int,
      boardingId: Int,
      categoryId: Int,
      accommodationId: Int
  )

  implicit final val outputTransform: Output => PropertyProviderEP = _.into[PropertyProviderEP]
    .withFieldComputed(_.cost, x => LookCostId(x.costId))
    .withFieldComputed(_.property, x => LookPartyId(x.propertyId))
    .withFieldComputed(_.supplier, x => LookPartyId(x.supplierId))
    .withFieldComputed(_.typeRoom, x => LookRoomTypeId(x.typeId))
    .withFieldComputed(_.boarding, x => LookBoardingId(x.boardingId))
    .withFieldComputed(_.categoryRoom, x => LookRoomCategoryId(x.categoryId))
    .withFieldComputed(_.accommodation, x => LookAccommodationId(x.accommodationId))
    .transform

  implicit final val itemR: PropertyProviderEP => Id = _.cost

  implicit final val itemP: PropertyProviderEP => Int = _ => 0

  implicit final val item0: PropertyProviderEP => CostBaseEP =
    _.into[CostBaseEP].transform

  implicit final val item1: PropertyProviderEP => Option[CostBaseEP] =
    (x: PropertyProviderEP) => item0(x).some
}
