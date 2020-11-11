package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import derevo.cats.order
import derevo.derive
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class RuleApplyItem(val value: Int, val name: String) extends IntEnumEntry

case object RuleApplyItem
    extends IntEnum[RuleApplyItem] with IntCirceEnum[RuleApplyItem] with LoggableIntEnum[RuleApplyItem] {
  override def values: immutable.IndexedSeq[RuleApplyItem] = findValues

  case object Undefined extends RuleApplyItem(value = -1, name = "undefined")

  case object LastNight extends RuleApplyItem(value = 0, name = "last night")

  case object MinPrice extends RuleApplyItem(value = 1, name = "min price")

  case object NoName extends RuleApplyItem(value = 2, name = "NO_NAME")

  case object MaxPrice extends RuleApplyItem(value = 3, name = "MAX_PRICE")
}
