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
sealed abstract class StatusItem(val value: Int, val name: String, val active: Boolean) extends IntEnumEntry

case object StatusItem extends IntEnum[StatusItem] with IntCirceEnum[StatusItem] with LoggableIntEnum[StatusItem] {
  override def values: immutable.IndexedSeq[StatusItem] = findValues

  case object Undefined extends StatusItem(value = -1, name = "undefined", active = true)

  case object New extends StatusItem(value = 0, name = "new", active = true)

  case object Wait extends StatusItem(value = 1, name = "wait", active = true)

  case object Confirmed extends StatusItem(value = 2, name = "confirmed", active = true)

  case object Cancel extends StatusItem(value = 3, name = "cancel", active = false)

  case object NotConfirmed extends StatusItem(value = 4, name = "not confirmed", active = false)

  case object WaitCancellation extends StatusItem(value = 5, name = "wait cancellation", active = false)

  case object SystemClean extends StatusItem(value = 6, name = "system clean", active = false)
}
