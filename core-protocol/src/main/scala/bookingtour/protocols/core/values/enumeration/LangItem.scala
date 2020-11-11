package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import derevo.cats.order
import derevo.derive
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.show

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, show)
sealed abstract class LangItem(val value: Int, val name: String) extends IntEnumEntry

case object LangItem extends IntEnum[LangItem] with IntCirceEnum[LangItem] with LoggableIntEnum[LangItem] {
  override def values: immutable.IndexedSeq[LangItem] = findValues

  final case object Ru extends LangItem(value = 0, name = "ru")

  final case object En extends LangItem(value = 1, name = "en")
}
