package bookingtour.core.doobie.modules

import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
trait SnapshotOps[Input] {
  def create(id: Input): Update0
}
