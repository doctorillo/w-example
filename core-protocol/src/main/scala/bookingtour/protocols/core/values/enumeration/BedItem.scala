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
sealed abstract class BedItem(val value: Int, val name: String) extends IntEnumEntry

case object BedItem extends IntEnum[BedItem] with IntCirceEnum[BedItem] with LoggableIntEnum[BedItem] {
  override def values: immutable.IndexedSeq[BedItem] = findValues

  final case object Main extends BedItem(value = 0, name = "main")

  final case object Extra extends BedItem(value = 1, name = "exb")

  final case object Without extends BedItem(value = 2, name = "without")
}
