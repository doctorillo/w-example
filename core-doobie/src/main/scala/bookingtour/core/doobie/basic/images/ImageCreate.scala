package bookingtour.core.doobie.basic.images

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{ImageE, LabelE}
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
final class ImageCreate private (
    table: String,
    getOps: GetOps[UUID, ImageE],
    labelOps: BatchCreateOps[LabelE.Create, LabelE]
) extends ByInputOps[ImageE.Create, ImageE] with CreateOps[ImageE.Create, ImageE]
    with BatchCreateOps[ImageE.Create, ImageE] with EnumerationToDoobieOps {

  def byInput(data: ImageE.Create): Query0[ImageE] = {
    (const(s"""SELECT DISTINCT
                  id,
                 data_id,
                 path,
                 'order',
                 updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"data_id = ${data.dataId} AND path = ${data.path}")
      .query[ImageE.Output]
      .map(ImageE.outputTransform)
  }

  def insert(item: ImageE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "path", "order") VALUES (${item.solverId}, ${item.dataId}, ${item.path}, ${item.position})""").update

  def runCreate(data: ImageE.Create): ConnectionIO[ImageE] = {
    for {
      a <- byInput(data).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- labelOps.runCreateList(LabelE.create(id, data.position.toString))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object ImageCreate {
  final def apply(
      table: String,
      labelOps: BatchCreateOps[LabelE.Create, LabelE]
  ): ByInputOps[ImageE.Create, ImageE]
    with CreateOps[ImageE.Create, ImageE] with BatchCreateOps[ImageE.Create, ImageE] =
    new ImageCreate(table, ImageGet(table), labelOps)
}
