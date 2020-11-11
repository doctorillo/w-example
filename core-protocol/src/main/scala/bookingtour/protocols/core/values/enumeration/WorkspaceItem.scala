package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class WorkspaceItem(val value: Int, val name: String) extends IntEnumEntry

case object WorkspaceItem
    extends IntEnum[WorkspaceItem] with IntCirceEnum[WorkspaceItem] with LoggableIntEnum[WorkspaceItem] {
  override def values: immutable.IndexedSeq[WorkspaceItem] = findValues

  final case object Provider extends WorkspaceItem(value = 0, name = "provider")

  final case object Customer extends WorkspaceItem(value = 1, name = "customer")

  final case object Supplier extends WorkspaceItem(value = 2, name = "supplier")

  final case object Client extends WorkspaceItem(value = 3, name = "client")
}
