package bookingtour.core.doobie.basic.enumvalues

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.EnumProjectionE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class EnumProjectionCreate private (table: String, getOps: GetOps[UUID, EnumProjectionE])
    extends ByInputOps[EnumProjectionE.Create, EnumProjectionE] with CreateOps[EnumProjectionE.Create, EnumProjectionE]
    with BatchCreateOps[EnumProjectionE.Create, EnumProjectionE] with EnumerationToDoobieOps {

  def byInput(data: EnumProjectionE.Create): Query0[EnumProjectionE] = {
    (const(s"""SELECT
                id,
                value_id,
                updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"value_id = ${data.valueId}")
      .query[EnumProjectionE.Output]
      .map(EnumProjectionE.outputTransform)
  }

  def insert(item: EnumProjectionE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "value_id") VALUES (${item.solverId}, ${item.valueId})""").update

  def runCreate(data: EnumProjectionE.Create): ConnectionIO[EnumProjectionE] = {
    for {
      a <- byInput(data).option
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

object EnumProjectionCreate {
  final def apply(table: String): EnumProjectionCreate =
    new EnumProjectionCreate(table, EnumProjectionGet(table))
}
