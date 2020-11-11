package bookingtour.core.doobie.basic.images

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.ImageE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class ImageGet private (table: String) extends GetOps[UUID, ImageE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[ImageE] = {
    (const(s"""
    SELECT DISTINCT
       id,
       data_id,
       path,
       'order',
       updated
    FROM
    	$table
    WHERE
    	mark_as_delete = FALSE
    	AND """) ++ fr"id = $id").query[ImageE.Output].map(ImageE.outputTransform)
  }
}

object ImageGet {
  final def apply(table: String): GetOps[UUID, ImageE] = new ImageGet(table)
}
