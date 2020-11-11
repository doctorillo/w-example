package bookingtour.core.doobie.modules

import cats.instances.list._
import cats.syntax.traverse._
import doobie.ConnectionIO
import doobie.implicits._

/**
  * © Alexey Toroshchin 2020.
  */
trait BatchCreateOps[-Input, Output] {
  self: CreateOps[Input, Output] =>

  final def runCreateList(in: List[Input]): ConnectionIO[List[Output]] =
    in.traverse(runCreate)
}
