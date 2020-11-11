package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order, loggable)
sealed abstract class PriceViewMode(val value: Int, val name: String) extends IntEnumEntry

case object PriceViewMode
    extends IntEnum[PriceViewMode] with IntCirceEnum[PriceViewMode] with LoggableIntEnum[PriceViewMode] {
  override def values: immutable.IndexedSeq[PriceViewMode] = findValues

  case object PerNightPax extends PriceViewMode(value = 0, name = "pax / per night")

  case object PerRoom extends PriceViewMode(value = 1, name = "per room")
}
