package bookingtour.data.parties.sql.parties.parties

import java.util.UUID

import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.values.PartyPREP
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class PartyCreate private (
    getOps: GetOps[UUID, PartyPREP],
    syncOps: CreateOps[SyncE.Create, SyncE] with ByInputOps[SyncItem, SyncE]
) extends CreateOps[PartyPREP.Create, PartyPREP] with BatchCreateOps[PartyPREP.Create, PartyPREP] {

  def insert(data: PartyPREP.Create): Update0 =
    sql"INSERT INTO parties (mark_as_delete) VALUES (false)".update

  def runCreate(data: PartyPREP.Create): ConnectionIO[PartyPREP] = {
    for {
      a <- syncOps.byInput(data.sync).option
      b <- a match {
            case Some(value) =>
              getOps.get(value.dataId).unique

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- syncOps.insert(SyncE.Create(solverId = None, dataId = id, sync = data.sync)).run
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object PartyCreate {
  final def apply(
      tableSync: String
  ): CreateOps[PartyPREP.Create, PartyPREP] with BatchCreateOps[PartyPREP.Create, PartyPREP] =
    new PartyCreate(PartyGet(), SyncCreate(tableSync))
}
