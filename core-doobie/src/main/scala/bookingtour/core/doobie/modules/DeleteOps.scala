package bookingtour.core.doobie.modules

import doobie.ConnectionIO
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
trait DeleteOps[-Input] {
  def delete(data: Input): Update0

  def runDelete(data: Input): ConnectionIO[Unit]
}
