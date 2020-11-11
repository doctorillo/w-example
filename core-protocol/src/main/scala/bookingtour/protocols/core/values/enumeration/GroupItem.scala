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
sealed abstract class GroupItem(val value: Int, val category: GroupCategoryItem, val name: String) extends IntEnumEntry

case object GroupItem extends IntEnum[GroupItem] with IntCirceEnum[GroupItem] with LoggableIntEnum[GroupItem] {
  override def values: immutable.IndexedSeq[GroupItem] = findValues

  final case object Undefined extends GroupItem(value = 0, category = GroupCategoryItem.Undefined, name = "undefined")

  // STAFF
  final case object OfficeManager
      extends GroupItem(value = 1, category = GroupCategoryItem.Staff, name = "office manager")

  final case object Guide extends GroupItem(value = 2, category = GroupCategoryItem.Staff, name = "guide")

  final case object Delegate extends GroupItem(value = 3, category = GroupCategoryItem.Staff, name = "delegate")

  final case object Driver extends GroupItem(value = 4, category = GroupCategoryItem.Staff, name = "driver")

  final case object External extends GroupItem(value = 5, category = GroupCategoryItem.Staff, name = "external")

  // SUPPLIER
  final case object SupplierMain extends GroupItem(value = 6, category = GroupCategoryItem.Supplier, name = "main")
  // CUSTOMER

  final case object SupplierOther extends GroupItem(value = 7, category = GroupCategoryItem.Supplier, name = "other")

  // INTER LOOK PARTNERS
  final case object CustomerDistributor
      extends GroupItem(value = 8, category = GroupCategoryItem.Customer, name = "distributor")

  // DIRECT SALE
  final case object CustomerAgent extends GroupItem(value = 9, category = GroupCategoryItem.Customer, name = "agent")

  // ATTACH SALE
  final case object CustomerDialer extends GroupItem(value = 10, category = GroupCategoryItem.Customer, name = "dialer")
}
