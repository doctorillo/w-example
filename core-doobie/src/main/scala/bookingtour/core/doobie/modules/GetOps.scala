package bookingtour.core.doobie.modules

import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
trait GetOps[-Ident, Value] {
  def get(id: Ident): Query0[Value]
}
