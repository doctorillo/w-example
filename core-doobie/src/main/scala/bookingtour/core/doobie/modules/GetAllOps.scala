package bookingtour.core.doobie.modules

import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
trait GetAllOps[Value] {
  def getAll(): Query0[Value]
}
