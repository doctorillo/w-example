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
sealed abstract class AppItem(val value: Int, val name: String) extends IntEnumEntry

case object AppItem extends IntEnum[AppItem] with IntCirceEnum[AppItem] with LoggableIntEnum[AppItem] {
  override def values: immutable.IndexedSeq[AppItem] = findValues

  final case object Partner extends AppItem(value = 0, name = "partner")

  final case object Direct extends AppItem(value = 1, name = "direct")
}
