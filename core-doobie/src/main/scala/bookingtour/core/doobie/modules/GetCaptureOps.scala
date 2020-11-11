package bookingtour.core.doobie.modules

import bookingtour.protocols.core.db.DbEventPayload
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
trait GetCaptureOps[Ident, Version] {
  def changes(version: Version): Query0[DbEventPayload.BaseEntity[Ident, Version]]
}
