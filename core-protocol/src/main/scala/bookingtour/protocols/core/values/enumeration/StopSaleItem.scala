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
sealed abstract class StopSaleItem(val value: Int, val name: String) extends IntEnumEntry

case object StopSaleItem
    extends IntEnum[StopSaleItem] with IntCirceEnum[StopSaleItem] with LoggableIntEnum[StopSaleItem] {
  override def values: immutable.IndexedSeq[StopSaleItem] = findValues

  final case object Undefined extends StopSaleItem(value = -1, name = "undefined")

  final case object Allotment extends StopSaleItem(value = 0, name = "allotment")

  final case object All extends StopSaleItem(value = 1, name = "all")

  final case object NotAssigned extends StopSaleItem(value = 2, name = "not assigned")

  final case object Region extends StopSaleItem(value = 3, name = "region")
}
