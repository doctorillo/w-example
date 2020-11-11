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
sealed abstract class GenderItem(val value: Int, val name: String) extends IntEnumEntry

case object GenderItem extends IntEnum[GenderItem] with IntCirceEnum[GenderItem] with LoggableIntEnum[GenderItem] {
  override def values: immutable.IndexedSeq[GenderItem] = findValues

  final case object Male extends GenderItem(value = 0, name = "adult")

  final case object Female extends GenderItem(value = 1, name = "female")

  final case object Unknown extends GenderItem(value = 2, name = "unknown")

  final case object Unknown1 extends GenderItem(value = 3, name = "unknown")

  final case object Unknown2 extends GenderItem(value = 4, name = "unknown")
}
