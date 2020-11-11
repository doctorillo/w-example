package bookingtour.core.doobie.modules

import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
trait ByInputOps[-Input, Output] {
  def byInput(data: Input): Query0[Output]
}
