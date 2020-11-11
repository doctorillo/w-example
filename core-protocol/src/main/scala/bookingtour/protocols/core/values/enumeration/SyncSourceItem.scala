package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import derevo.cats.order
import derevo.derive
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class SyncSourceItem(val value: Int, val name: String) extends IntEnumEntry

case object SyncSourceItem
    extends IntEnum[SyncSourceItem] with IntCirceEnum[SyncSourceItem] with LoggableIntEnum[SyncSourceItem] {
  override def values: immutable.IndexedSeq[SyncSourceItem] = findValues

  final case object InterLook extends SyncSourceItem(value = 0, name = "inter look")

  final case object PragueOperation extends SyncSourceItem(value = 1, name = "prague operation")

  final case object Parties extends SyncSourceItem(value = 2, name = "parties")

  final case object Properties extends SyncSourceItem(value = 3, name = "properties")

  final case object InterLookAccommodationPrices
      extends SyncSourceItem(value = 4, name = "inter look accommodation prices")

  final case object InterLookOrders extends SyncSourceItem(value = 5, name = "inter look orders")

  final case object Orders extends SyncSourceItem(value = 6, name = "orders")

  final case object InterLookRich extends SyncSourceItem(value = 7, name = "inter look rich")
}
