package bookingtour.protocols.properties.agg

import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.core.values.api.{DescriptionAPI, LabelAPI}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.properties.newTypes._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookRoomCategoryId, LookRoomTypeId}
import bookingtour.protocols.parties.api.queries.QueryGuest
import bookingtour.protocols.properties.values.properties.{RoomCategoryPPEP, RoomTypePPEP}
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class RoomUnitProduct(
    id: RoomUnitId,
    propertyId: PropertyId,
    typeId: RoomTypeId,
    typeSyncs: List[SyncItem],
    typeLabels: List[LabelAPI],
    categoryId: RoomCategoryId,
    categorySyncs: List[SyncItem],
    categoryLabels: List[LabelAPI],
    onMain: PaxOnMain,
    onExb: PaxOnExtraBed,
    descriptions: List[DescriptionAPI],
    facilities: List[FacilityId]
)

object RoomUnitProduct {
  type Id = RoomUnitId

  implicit final val itemR0: RoomUnitProduct => Id = _.id

  implicit final val itemP0: RoomUnitProduct => PropertyId = _.propertyId

  final case class RoomUnitProductOp(
      id: RoomUnitId,
      propertyId: PropertyId,
      typeId: RoomTypeId,
      typeSync: LookRoomTypeId,
      categoryId: RoomCategoryId,
      categorySync: LookRoomCategoryId,
      onMain: PaxOnMain,
      onExb: PaxOnExtraBed
  )

  implicit final class RoomUnitProductOps(private val self: RoomUnitProduct) {
    def toOp: Option[RoomUnitProductOp] = {
      for {
        typeSync     <- self.typeSyncs.flatMap(RoomTypePPEP.fromSyncItem(_).toList).headOption
        categorySync <- self.categorySyncs.flatMap(RoomCategoryPPEP.fromSyncItem(_).toList).headOption
      } yield RoomUnitProductOp(
        id = self.id,
        propertyId = self.propertyId,
        typeId = self.typeId,
        typeSync = typeSync,
        categoryId = self.categoryId,
        categorySync = categorySync,
        onMain = self.onMain,
        onExb = self.onExb
      )
    }
  }
}
