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
sealed abstract class ContextItem(val value: Int, val name: String) extends IntEnumEntry

case object ContextItem extends IntEnum[ContextItem] with IntCirceEnum[ContextItem] with LoggableIntEnum[ContextItem] {
  override def values: immutable.IndexedSeq[ContextItem] = findValues

  case object Accommodation extends ContextItem(value = 1, name = "Accommodation")

  case object Excursion extends ContextItem(value = 2, name = "Excursion")

  case object Transfer extends ContextItem(value = 3, name = "Transfer")

  case object Extra extends ContextItem(value = 4, name = "Extra")

  case object Spa extends ContextItem(value = 5, name = "Spa")

  case object Transport extends ContextItem(value = 6, name = "Transport")

  case object All extends ContextItem(value = 7, name = "All")

  final val BaseItems: List[ContextItem] = List(Accommodation, Excursion, Transfer)
}
