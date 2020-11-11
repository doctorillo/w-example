package bookingtour.core.doobie.modules

import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
trait GetByIdListOps[-Ident, Value] {
  def get(id: List[Ident]): Query0[Value]
}
