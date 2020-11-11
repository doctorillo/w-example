package bookingtour.data.parties.sql.geo.regions

import java.util.UUID

import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{LabelE, SyncE}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.values.RegionPREP
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class RegionCreate private (
    getOps: GetOps[UUID, RegionPREP],
    syncOps: CreateOps[SyncE.Create, SyncE] with ByInputOps[SyncItem, SyncE],
    labelOps: CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE]
) extends CreateOps[RegionPREP.Create, RegionPREP] with BatchCreateOps[RegionPREP.Create, RegionPREP] {

  def insert(data: RegionPREP.Create): Update0 =
    sql"""INSERT INTO regions ("country_id", "name") VALUES (${data.countryId}, ${data.name})""".update

  def runCreate(data: RegionPREP.Create): ConnectionIO[RegionPREP] = {
    for {
      a <- syncOps.byInput(data.sync).option
      b <- a match {
            case Some(value) =>
              for {
                _ <- labelOps.runCreateList(LabelE.create(value.dataId.x, data.name))
                c <- getOps.get(value.dataId.x).unique
              } yield c

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

object RegionCreate {
  final def apply(
      tableSync: String,
      tableLabel: String
  ): CreateOps[RegionPREP.Create, RegionPREP] with BatchCreateOps[RegionPREP.Create, RegionPREP] =
    new RegionCreate(RegionGet(), SyncCreate(tableSync), LabelCreate(tableLabel))
}
