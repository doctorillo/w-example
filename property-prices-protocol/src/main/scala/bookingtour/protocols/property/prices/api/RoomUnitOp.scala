package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.core.values.enumeration.SyncItem._
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomCategoryId, LookRoomTypeId}
import bookingtour.protocols.properties.agg.RoomUnitProduct
import bookingtour.protocols.properties.api.PropertyCardProduct
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomCategoryId, RoomTypeId, RoomUnitId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class RoomUnitOp(
    id: RoomUnitId,
    propertyId: PropertyId,
    propertySync: LookPartyId,
    typeId: RoomTypeId,
    typeSync: LookRoomTypeId,
    categoryId: RoomCategoryId,
    categorySync: LookRoomCategoryId,
    onMain: PaxOnMain,
    onExb: PaxOnExtraBed
)

object RoomUnitOp {
  final type Id = RoomUnitId

  implicit final val itemR0: RoomUnitOp => Id = _.id

  implicit final val itemP0: RoomUnitOp => Int = _ => 0

  implicit final val itemT0: (List[SyncItem], RoomUnitProduct) => RoomUnitOp =
    (propertySyncs, unit) =>
      unit
        .into[RoomUnitOp]
        .withFieldComputed(_.propertySync, _ => LookPartyId(asInterLook(propertySyncs).head.id))
        .withFieldComputed(_.typeSync, x => LookRoomTypeId(asInterLook(x.typeSyncs).head.id))
        .withFieldComputed(
          _.categorySync,
          x => LookRoomCategoryId(asInterLook(x.categorySyncs).head.id)
        )
        .transform

  implicit final val itemT1: PropertyCardProduct => List[RoomUnitOp] = x =>
    x.roomUnits.map(z => itemT0(x.propertySync, z))
}
