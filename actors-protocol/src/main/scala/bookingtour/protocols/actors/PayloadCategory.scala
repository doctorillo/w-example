package bookingtour.protocols.actors

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class PayloadCategory(val value: Int) extends IntEnumEntry

case object PayloadCategory extends IntEnum[PayloadCategory] with IntCirceEnum[PayloadCategory] {
  override def values: immutable.IndexedSeq[PayloadCategory] = findValues

  case object Update extends PayloadCategory(value = 0)

  case object Delete extends PayloadCategory(value = 1)

  case object Flush extends PayloadCategory(value = 2)

  implicit final val payloadCategoryO: Order[PayloadCategory] =
    (x: PayloadCategory, y: PayloadCategory) => x.value.compare(y.value)
}
