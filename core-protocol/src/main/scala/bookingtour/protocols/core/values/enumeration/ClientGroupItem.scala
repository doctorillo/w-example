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
sealed abstract class ClientGroupItem(val value: Int, val name: String) extends IntEnumEntry

case object ClientGroupItem
    extends IntEnum[ClientGroupItem] with IntCirceEnum[ClientGroupItem] with LoggableIntEnum[ClientGroupItem] {
  override def values: immutable.IndexedSeq[ClientGroupItem] = findValues

  final case object Solo extends ClientGroupItem(value = 0, name = "solo")

  final case object Duo extends ClientGroupItem(value = 1, name = "duo")

  final case object Family extends ClientGroupItem(value = 2, name = "family")

  final case object Group extends ClientGroupItem(value = 3, name = "group")
}
