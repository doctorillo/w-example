package bookingtour.protocols.business.rules.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ConditionCategoryItem(val value: Int, val name: String) extends IntEnumEntry

case object ConditionCategoryItem extends IntEnum[ConditionCategoryItem] with IntCirceEnum[ConditionCategoryItem] {
  override def values: immutable.IndexedSeq[ConditionCategoryItem] = findValues

  case object Undefined extends ConditionCategoryItem(value = 0, name = "undefined")

  case object All extends ConditionCategoryItem(value = 1, name = "all")

  case object Custom extends ConditionCategoryItem(value = 2, name = "custom")

  implicit final val catsO: Order[ConditionCategoryItem] =
    (x: ConditionCategoryItem, y: ConditionCategoryItem) => x.value.compare(y.value)
}
