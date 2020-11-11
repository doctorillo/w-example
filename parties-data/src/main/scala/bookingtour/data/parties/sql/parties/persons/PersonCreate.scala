package bookingtour.data.parties.sql.parties.persons

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.PersonPREP
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
final class PersonCreate private (getOps: GetOps[UUID, PersonPREP])
    extends ByInputOps[UUID, PersonPREP] with CreateOps[PersonPREP.Create, PersonPREP]
    with BatchCreateOps[PersonPREP.Create, PersonPREP] {

  def insert(data: PersonPREP.Create): Update0 =
    sql"""INSERT INTO persons ("data_id", "first_name", "last_name") VALUES (${data.partyId}, ${data.firstName}, ${data.lastName})""".update

  def byInput(partyId: UUID): query.Query0[PersonPREP] = {
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
            AND data_id = $partyId""".query[PersonPREP.Output].map(PersonPREP.outputTransform)
  }

  def runCreate(data: PersonPREP.Create): ConnectionIO[PersonPREP] = {
    for {
      a <- byInput(data.partyId).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object PersonCreate {
  final def apply(): PersonCreate =
    new PersonCreate(PersonGet())
}
