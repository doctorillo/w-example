package bookingtour.protocols.properties.values.properties

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomTypeId}
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomTypeId}
import cats.syntax.option._
import derevo.cats.order
import derevo.derive
import io.scalaland.chimney.dsl._
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order)
final case class RoomTypePPEP(
    id: RoomTypeId,
    dataId: PropertyId,
    code: String,
    mainPlaces: PaxOnMain,
    exbPlaces: PaxOnExtraBed,
    stamp: Instant
)

object RoomTypePPEP {
  type Id = RoomTypeId

  implicit final val itemR0: RoomTypePPEP => Id = _.id

  implicit final val itemR1: RoomTypePPEP => Instant = _.stamp

  implicit final val itemP0: RoomTypePPEP => PropertyId = _.dataId

  final case class Output(
      id: UUID,
      dataId: UUID,
      code: String,
      mainPlaces: Int,
      exbPlaces: Int,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => RoomTypePPEP = _.into[RoomTypePPEP]
    .withFieldComputed(_.id, x => RoomTypeId(x.id))
    .withFieldComputed(_.dataId, x => PropertyId(x.dataId))
    .withFieldComputed(_.mainPlaces, x => PaxOnMain(x.mainPlaces))
    .withFieldComputed(_.exbPlaces, x => PaxOnExtraBed(x.exbPlaces))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      dataId: UUID,
      code: String,
      name: String,
      mainPlaces: Int,
      exbPlaces: Int,
      sync: SyncItem,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final val toSync: (
      LookRoomTypeId,
      LookPartyId
  ) => SyncItem.InterLook =
    (roomTypeSync, propertySync) =>
      SyncItem.InterLook(
        id = roomTypeSync.x,
        categoryId = propertySync.x.some
      )
  final case class RoomTypeInterLookSync(
      id: RoomTypeId,
      sync: LookRoomTypeId,
      propertySync: LookPartyId
  )

  final val fromSync: SyncE => Option[RoomTypeInterLookSync] =
    _.askInterLook.flatMap {
      case (dataId, syncId, categoryId) =>
        categoryId.map(x =>
          RoomTypeInterLookSync(
            id = RoomTypeId(dataId.x),
            sync = LookRoomTypeId(syncId),
            LookPartyId(x)
          )
        )
    }

  final val fromSyncItem: SyncItem => Option[LookRoomTypeId] = _.toInterLook.map(x => LookRoomTypeId(x.id))
}
