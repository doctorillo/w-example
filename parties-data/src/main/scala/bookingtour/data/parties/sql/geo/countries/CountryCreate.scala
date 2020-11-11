package bookingtour.data.parties.sql.geo.countries

import java.util.UUID

import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{LabelE, SyncE}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.values.CountryPREP
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class CountryCreate private (
    getOps: GetOps[UUID, CountryPREP],
    syncOps: CreateOps[SyncE.Create, SyncE] with ByInputOps[SyncItem, SyncE],
    labelOps: CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE]
) extends CreateOps[CountryPREP.Create, CountryPREP] with BatchCreateOps[CountryPREP.Create, CountryPREP] {

  def insert(data: CountryPREP.Create): Update0 =
    sql"""INSERT INTO countries ("name") VALUES (${data.name})""".update

  def runCreate(data: CountryPREP.Create): ConnectionIO[CountryPREP] = {
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

object CountryCreate {
  final def apply(
      tableSync: String,
      tableLabel: String
  ): CreateOps[CountryPREP.Create, CountryPREP] with BatchCreateOps[CountryPREP.Create, CountryPREP] =
    new CountryCreate(CountryGet(), SyncCreate(tableSync), LabelCreate(tableLabel))
}
