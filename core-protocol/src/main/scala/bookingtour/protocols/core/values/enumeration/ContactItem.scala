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
sealed abstract class ContactItem(val value: Int, val name: String) extends IntEnumEntry

case object ContactItem extends IntEnum[ContactItem] with IntCirceEnum[ContactItem] with LoggableIntEnum[ContactItem] {
  override def values: immutable.IndexedSeq[ContactItem] = findValues

  case object Undefined extends ContactItem(value = 0, name = "Undefined")

  case object Phone extends ContactItem(value = 1, name = "Phone")

  case object Email extends ContactItem(value = 2, name = "Email")

  case object Web extends ContactItem(value = 3, name = "Web")

  case object Skype extends ContactItem(value = 4, name = "Skype")
}
