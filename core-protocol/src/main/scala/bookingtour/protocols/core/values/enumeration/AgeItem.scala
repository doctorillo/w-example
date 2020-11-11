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
sealed abstract class AgeItem(val value: Int, val name: String) extends IntEnumEntry

case object AgeItem extends IntEnum[AgeItem] with IntCirceEnum[AgeItem] with LoggableIntEnum[AgeItem] {
  override def values: immutable.IndexedSeq[AgeItem] = findValues

  final case object Adult extends AgeItem(value = 0, name = "adult")

  final case object Female extends AgeItem(value = 1, name = "female")

  final case object Child extends AgeItem(value = 2, name = "child")

  final case object Infant extends AgeItem(value = 3, name = "infant")

  final case object Unknown extends AgeItem(value = 4, name = "unknown")
}
