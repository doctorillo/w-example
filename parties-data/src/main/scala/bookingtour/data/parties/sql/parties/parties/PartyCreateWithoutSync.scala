package bookingtour.data.parties.sql.parties.parties

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.PartyPREP
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class PartyCreateWithoutSync private (
    getOps: GetOps[UUID, PartyPREP]
) extends CreateOps[PartyPREP.CreateWithoutSync, PartyPREP]
    with BatchCreateOps[PartyPREP.CreateWithoutSync, PartyPREP] {

  def insert(data: PartyPREP.CreateWithoutSync): Update0 =
    sql"INSERT INTO parties (mark_as_delete) VALUES (false)".update

  def runCreate(data: PartyPREP.CreateWithoutSync): ConnectionIO[PartyPREP] = {
    for {
      id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
      b  <- getOps.get(id).unique
    } yield b
  }
}

object PartyCreateWithoutSync {
  final def apply()
      : CreateOps[PartyPREP.CreateWithoutSync, PartyPREP] with BatchCreateOps[PartyPREP.CreateWithoutSync, PartyPREP] =
    new PartyCreateWithoutSync(PartyGet())
}
