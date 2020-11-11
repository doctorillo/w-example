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
sealed abstract class BookingRequestItem(val value: Int, val name: String) extends IntEnumEntry

case object BookingRequestItem
    extends IntEnum[BookingRequestItem] with IntCirceEnum[BookingRequestItem] with LoggableIntEnum[BookingRequestItem] {
  override def values: immutable.IndexedSeq[BookingRequestItem] = findValues

  final case object Undefined extends BookingRequestItem(value = 0, name = "undefined")

  final case object Create extends BookingRequestItem(value = 1, name = "create")

  final case object Notes extends BookingRequestItem(value = 2, name = "notes")

  final case object Change extends BookingRequestItem(value = 3, name = "change")
}
