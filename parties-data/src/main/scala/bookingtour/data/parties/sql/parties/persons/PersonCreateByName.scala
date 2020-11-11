package bookingtour.data.parties.sql.parties.persons

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.data.parties.sql.parties.parties.PartyCreateWithoutSync
import bookingtour.protocols.parties.values.{PartyPREP, PersonPREP}
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.query
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class PersonCreateByName private (
    getOps: GetOps[UUID, PersonPREP],
    createOps: CreateOps[PartyPREP.CreateWithoutSync, PartyPREP]
) extends ByInputOps[PersonPREP.Create, PersonPREP] with CreateOps[PersonPREP.Create, PersonPREP]
    with BatchCreateOps[PersonPREP.Create, PersonPREP] {

  def insert(data: PersonPREP.Create): Update0 =
    sql"""INSERT INTO persons ("data_id", "first_name", "last_name") VALUES (${data.partyId}, ${data.firstName}, ${data.lastName})""".update

  def byInput(data: PersonPREP.Create): query.Query0[PersonPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            first_name,
            last_name,
            updated
           FROM
            persons
           WHERE
            mark_as_delete = FALSE
            AND first_name = ${data.firstName}
            AND last_name = ${data.lastName}"""
      .query[PersonPREP.Output]
      .map(PersonPREP.outputTransform)
  }

  def runCreate(data: PersonPREP.Create): ConnectionIO[PersonPREP] = {
    for {
      a <- byInput(data).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                party <- createOps.runCreate(PartyPREP.CreateWithoutSync(data.lastName))
                id    <- insert(data.copy(partyId = party.id)).withUniqueGeneratedKeys[UUID]("id")
                c     <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object PersonCreateByName {
  final def apply(): PersonCreateByName =
    new PersonCreateByName(PersonGet(), PartyCreateWithoutSync())
}
