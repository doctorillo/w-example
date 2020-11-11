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
sealed abstract class AmountApplyItem(val value: Int, val name: String) extends IntEnumEntry

case object AmountApplyItem
    extends IntEnum[AmountApplyItem] with IntCirceEnum[AmountApplyItem] with LoggableIntEnum[AmountApplyItem] {
  override def values: immutable.IndexedSeq[AmountApplyItem] = findValues

  final case object Undefined extends AmountApplyItem(value = 0, name = "Undefined")

  final case object PerPax extends AmountApplyItem(value = 1, name = "PerPax")

  final case object PerEvent extends AmountApplyItem(value = 2, name = "PerUnit")
}
