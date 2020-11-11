package bookingtour.protocols.core.values.enumeration

import bookingtour.protocols.core.values.db.EnumProjectionE

import scala.collection.immutable
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order, loggable)
sealed abstract class ExcursionTagItem(val value: Int, val name: String) extends IntEnumEntry

case object ExcursionTagItem
    extends IntEnum[ExcursionTagItem] with IntCirceEnum[ExcursionTagItem] with LoggableIntEnum[ExcursionTagItem] {
  override def values: immutable.IndexedSeq[ExcursionTagItem] = findValues

  case object Walk      extends ExcursionTagItem(value = 1, name = "пешеходная")
  case object Bus       extends ExcursionTagItem(value = 2, name = "автобусная")
  case object River     extends ExcursionTagItem(value = 3, name = "речная")
  case object Snacks    extends ExcursionTagItem(value = 4, name = "с питанием")
  case object Evening   extends ExcursionTagItem(value = 5, name = "вечерние")
  case object TwoDays   extends ExcursionTagItem(value = 6, name = "2-х дневные")
  case object ThreeDays extends ExcursionTagItem(value = 7, name = "3-х дневные")
  case object FourDays  extends ExcursionTagItem(value = 8, name = "4-х дневные")

  val toCreateWithLabel: List[EnumProjectionE.CreateWithLabel] =
    ExcursionTagItem.values.toList.map(x => EnumProjectionE.CreateWithLabel(valueId = x.value, name = x.name))
}
