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
sealed abstract class PersonGroupItem(val value: Int) extends IntEnumEntry

case object PersonGroupItem
    extends IntEnum[PersonGroupItem] with IntCirceEnum[PersonGroupItem] with LoggableIntEnum[PersonGroupItem] {
  override def values: immutable.IndexedSeq[PersonGroupItem] = findValues

  case object Solo extends PersonGroupItem(value = 0)

  case object Duo extends PersonGroupItem(value = 1)

  case object Family extends PersonGroupItem(value = 2)

  case object Group extends PersonGroupItem(value = 3)
}
