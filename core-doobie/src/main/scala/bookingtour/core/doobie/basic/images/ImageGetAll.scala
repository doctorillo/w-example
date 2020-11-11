package bookingtour.core.doobie.basic.images

import bookingtour.core.doobie.modules.GetAllOps
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class ImageGetAll private (table: String) extends GetAllOps[ImageE] with EnumerationToDoobieOps {

  def getAll(): Query0[ImageE] = {
    const(s"""
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
    ORDER BY
      id""")
      .query[ImageE.Output]
      .map(ImageE.outputTransform)
  }
}

object ImageGetAll {
  final def apply(table: String): GetAllOps[ImageE] = new ImageGetAll(table)
}
