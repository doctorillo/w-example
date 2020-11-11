package bookingtour.data.parties.sql.geo.points

import java.util.UUID

import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{LabelE, SyncE}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.values.PickupPointPREP
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class PickupPointCreate private (
    getOps: GetOps[UUID, PickupPointPREP],
    syncOps: CreateOps[SyncE.Create, SyncE] with ByInputOps[SyncItem, SyncE],
    labelOps: CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE]
) extends CreateOps[PickupPointPREP.Create, PickupPointPREP]
    with BatchCreateOps[PickupPointPREP.Create, PickupPointPREP] {

  def insert(data: PickupPointPREP.Create): Update0 =
    sql"""INSERT INTO city_points ("city_id", "type_id", "name", "geo_location") VALUES (${data.cityId.x}, ${data.pointType.value}, ${data.name}, ${data.location})""".update

  def runCreate(data: PickupPointPREP.Create): ConnectionIO[PickupPointPREP] = {
    for {
      a <- syncOps.byInput(data.sync).option
      b <- a match {
            case Some(value) =>
              getOps.get(value.dataId).unique

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- syncOps.insert(SyncE.Create(dataId = id, sync = data.sync)).run
                _  <- labelOps.runCreateList(LabelE.create(id, data.name))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object PickupPointCreate {
  final def apply(
      tableSync: String,
      tableLabel: String
  ): CreateOps[PickupPointPREP.Create, PickupPointPREP] with BatchCreateOps[PickupPointPREP.Create, PickupPointPREP] =
    new PickupPointCreate(PickupPointGetOps(), SyncCreate(tableSync), LabelCreate(tableLabel))
}
