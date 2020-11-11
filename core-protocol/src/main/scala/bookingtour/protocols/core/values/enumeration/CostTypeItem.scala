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
sealed abstract class CostTypeItem(val value: Int, val name: String) extends IntEnumEntry

case object CostTypeItem
    extends IntEnum[CostTypeItem] with IntCirceEnum[CostTypeItem] with LoggableIntEnum[CostTypeItem] {
  override def values: immutable.IndexedSeq[CostTypeItem] = findValues

  final case object Undefined extends CostTypeItem(value = -1, name = "Undefined")

  final case object PerPerson extends CostTypeItem(value = 0, name = "per person")

  final case object PerRoom extends CostTypeItem(value = 1, name = "per room")

  final case object All extends CostTypeItem(value = 2, name = "all")
}
