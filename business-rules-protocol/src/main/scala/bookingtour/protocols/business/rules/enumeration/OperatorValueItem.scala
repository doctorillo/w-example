package bookingtour.protocols.business.rules.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class OperatorValueItem(val value: Int, val name: String) extends IntEnumEntry

case object OperatorValueItem extends IntEnum[OperatorValueItem] with IntCirceEnum[OperatorValueItem] {
  override def values: immutable.IndexedSeq[OperatorValueItem] = findValues

  case object Undefined extends OperatorValueItem(value = 0, name = "undefined")

  case object Number extends OperatorValueItem(value = 1, name = "number")

  case object Percent extends OperatorValueItem(value = 2, name = "percent")

  implicit final val catsO: Order[OperatorValueItem] =
    (x: OperatorValueItem, y: OperatorValueItem) => x.value.compare(y.value)
}
