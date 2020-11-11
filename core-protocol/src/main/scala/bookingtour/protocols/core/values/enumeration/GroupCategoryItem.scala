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
sealed abstract class GroupCategoryItem(val value: Int, val name: String) extends IntEnumEntry

case object GroupCategoryItem
    extends IntEnum[GroupCategoryItem] with IntCirceEnum[GroupCategoryItem] with LoggableIntEnum[GroupCategoryItem] {
  override def values: immutable.IndexedSeq[GroupCategoryItem] = findValues

  final case object Undefined extends GroupCategoryItem(value = 0, name = "undefined")

  final case object Supplier extends GroupCategoryItem(value = 1, name = "supplier")

  final case object Customer extends GroupCategoryItem(value = 2, name = "customer")

  final case object Staff extends GroupCategoryItem(value = 3, name = "staff")
}
