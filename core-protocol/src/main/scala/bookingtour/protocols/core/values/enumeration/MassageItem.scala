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
sealed abstract class MassageItem(val value: Int, val name: String) extends IntEnumEntry

case object MassageItem extends IntEnum[MassageItem] with IntCirceEnum[MassageItem] with LoggableIntEnum[MassageItem] {
  override def values: immutable.IndexedSeq[MassageItem] = findValues

  case object AntiCellulite extends MassageItem(value = 1, name = "антицеллюлитный массаж")
  case object Aromatic      extends MassageItem(value = 2, name = "арома массаж")
  case object Classic       extends MassageItem(value = 3, name = "классический массаж")
  case object Facial        extends MassageItem(value = 4, name = "массаж лица")
  case object Honey         extends MassageItem(value = 5, name = "медовый массаж")
  case object Underwater    extends MassageItem(value = 6, name = "подводный массаж")
  case object Reflex        extends MassageItem(value = 10, name = "рефлекторный массаж")
}
