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
sealed abstract class DateRangeCategory(val value: Int, val name: String) extends IntEnumEntry

case object DateRangeCategory
    extends IntEnum[DateRangeCategory] with IntCirceEnum[DateRangeCategory] with LoggableIntEnum[DateRangeCategory] {
  override def values: immutable.IndexedSeq[DateRangeCategory] = findValues

  case object Undefined extends DateRangeCategory(value = 0, name = "Undefined")

  case object ByDateFrom extends DateRangeCategory(value = 1, name = "ByDateFrom")

  case object ByDateTo extends DateRangeCategory(value = 2, name = "ByDateTo")

  case object InRange extends DateRangeCategory(value = 3, name = "InRange")

  case object ByCreate extends DateRangeCategory(value = 4, name = "ByCreate")
}
