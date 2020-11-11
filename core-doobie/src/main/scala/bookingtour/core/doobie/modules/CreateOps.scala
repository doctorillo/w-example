package bookingtour.core.doobie.modules

import doobie.free.connection.ConnectionIO
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
trait CreateOps[-Input, Output] {
  def insert(data: Input): Update0

  def runCreate(data: Input): ConnectionIO[Output]
}
