package bookingtour.core.doobie.modules

import doobie.ConnectionIO
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
trait UpdateOps[-Input, Output] {
  def update(data: Input): Update0

  def runUpdate(data: Input): ConnectionIO[Output]
}
