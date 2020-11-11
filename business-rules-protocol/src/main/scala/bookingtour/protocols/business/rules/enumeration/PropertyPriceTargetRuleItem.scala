package bookingtour.protocols.business.rules.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class PropertyPriceTargetRuleItem(val value: Int, val name: String) extends IntEnumEntry

case object PropertyPriceTargetRuleItem
    extends IntEnum[PropertyPriceTargetRuleItem] with IntCirceEnum[PropertyPriceTargetRuleItem] {
  override def values: immutable.IndexedSeq[PropertyPriceTargetRuleItem] = findValues

  case object Undefined extends PropertyPriceTargetRuleItem(value = 0, name = "undefined")

  case object Cost extends PropertyPriceTargetRuleItem(value = 1, name = "cost")

  case object Total extends PropertyPriceTargetRuleItem(value = 2, name = "total")

  case object Profit extends PropertyPriceTargetRuleItem(value = 3, name = "profit")

  case object LowNight extends PropertyPriceTargetRuleItem(value = 4, name = "low-night")

  case object HighNight extends PropertyPriceTargetRuleItem(value = 5, name = "high-night")

  case object FirstNight extends PropertyPriceTargetRuleItem(value = 6, name = "first-night")

  case object LastNight extends PropertyPriceTargetRuleItem(value = 7, name = "last-night")

  implicit final val catsO: Order[PropertyPriceTargetRuleItem] =
    (x: PropertyPriceTargetRuleItem, y: PropertyPriceTargetRuleItem) => x.value.compare(y.value)
}
