package bookingtour.protocols.core.values.db.params

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.SyncItem

/**
  * © Alexey Toroshchin 2019.
  */
final case class SyncP(dataId: UUID, sync: SyncItem)
