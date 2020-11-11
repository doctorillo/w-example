package bookingtour.protocols.properties.values.properties

import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomCategoryId}
import bookingtour.protocols.properties.newTypes.RoomCategoryId
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
object RoomCategoryPPEP {
  final val toSync: (
      LookRoomCategoryId,
      LookPartyId
  ) => SyncItem.InterLook =
    (syncId, propertySync) =>
      SyncItem.InterLook(
        id = syncId.x,
        categoryId = Some(propertySync.x)
      )
  final case class RoomCategoryInterLookSync(
      id: RoomCategoryId,
      sync: LookRoomCategoryId,
      propertySync: LookPartyId
  )

  final val fromSync: SyncE => Option[RoomCategoryInterLookSync] =
    _.askInterLook.flatMap {
      case (dataId, syncId, categoryId) =>
        categoryId.map(x =>
          RoomCategoryInterLookSync(
            id = RoomCategoryId(dataId.x),
            sync = LookRoomCategoryId(syncId),
            propertySync = LookPartyId(x)
          )
        )
    }

  final val fromSyncItem: SyncItem => Option[LookRoomCategoryId] = _.toInterLook.map(x => LookRoomCategoryId(x.id))
}
