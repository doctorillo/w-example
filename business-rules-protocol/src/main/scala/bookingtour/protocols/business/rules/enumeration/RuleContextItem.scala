package bookingtour.protocols.business.rules.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class RuleContextItem(val value: Int, val name: String) extends IntEnumEntry

case object RuleContextItem extends IntEnum[RuleContextItem] with IntCirceEnum[RuleContextItem] {
  override def values: immutable.IndexedSeq[RuleContextItem] = findValues

  case object Undefined extends RuleContextItem(value = 0, name = "undefined")

  case object Price extends RuleContextItem(value = 1, name = "price")

  case object Bonus extends RuleContextItem(value = 2, name = "bonus")

  case object Payment extends RuleContextItem(value = 3, name = "payment")

  case object Penalty extends RuleContextItem(value = 4, name = "penalty")

  implicit final val catsO: Order[RuleContextItem] =
    (x: RuleContextItem, y: RuleContextItem) => x.value.compare(y.value)
}
