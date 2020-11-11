package bookingtour.core.doobie.modules

import cats.instances.list._
import cats.syntax.traverse._
import doobie.ConnectionIO
import doobie.implicits._

/**
  * © Alexey Toroshchin 2020.
  */
trait BatchDeleteOps[-Input] {
  self: DeleteOps[Input] =>

  final def runDeleteList(in: List[Input]): ConnectionIO[Unit] =
    in.traverse(runDelete).map(_ => ())
}
