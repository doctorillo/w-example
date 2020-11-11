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
sealed abstract class AddressItem(val value: Int, val name: String) extends IntEnumEntry

case object AddressItem extends IntEnum[AddressItem] with IntCirceEnum[AddressItem] with LoggableIntEnum[AddressItem] {
  override def values: immutable.IndexedSeq[AddressItem] = findValues

  final case object General extends AddressItem(value = 0, name = "general")

  final case object Correspondence extends AddressItem(value = 1, name = "correspondence")

  final case object Real extends AddressItem(value = 2, name = "real")
}
