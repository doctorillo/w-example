package bookingtour.protocols.business.rules.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class OperatorRuleItem(val value: Int, val name: String) extends IntEnumEntry

case object OperatorRuleItem extends IntEnum[OperatorRuleItem] with IntCirceEnum[OperatorRuleItem] {
  override def values: immutable.IndexedSeq[OperatorRuleItem] = findValues

  case object Undefined extends OperatorRuleItem(value = 0, name = "undefined")

  case object Plus extends OperatorRuleItem(value = 1, name = "plus")

  case object Minus extends OperatorRuleItem(value = 2, name = "minus")

  case object Multiply extends OperatorRuleItem(value = 3, name = "multiply")

  implicit final val catsO: Order[OperatorRuleItem] =
    (x: OperatorRuleItem, y: OperatorRuleItem) => x.value.compare(y.value)
}
