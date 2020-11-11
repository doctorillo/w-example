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
sealed abstract class ContextRoleItem(val value: Int, val name: String) extends IntEnumEntry

case object ContextRoleItem
    extends IntEnum[ContextRoleItem] with IntCirceEnum[ContextRoleItem] with LoggableIntEnum[ContextRoleItem] {
  override def values: immutable.IndexedSeq[ContextRoleItem] = findValues

  final case object Provider extends ContextRoleItem(value = 0, name = "provider")

  final case object Supplier extends ContextRoleItem(value = 1, name = "supplier")

  final case object Customer extends ContextRoleItem(value = 2, name = "customer")

  final case object Client extends ContextRoleItem(value = 3, name = "client")

  final case object CustomerGroup extends ContextRoleItem(value = 4, name = "customer-group")
}
