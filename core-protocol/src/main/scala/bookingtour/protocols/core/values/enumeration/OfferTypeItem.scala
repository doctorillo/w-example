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
sealed abstract class OfferTypeItem(val value: Int, val name: String) extends IntEnumEntry

case object OfferTypeItem
    extends IntEnum[OfferTypeItem] with IntCirceEnum[OfferTypeItem] with LoggableIntEnum[OfferTypeItem] {
  override def values: immutable.IndexedSeq[OfferTypeItem] = findValues

  final case object Ordinary extends OfferTypeItem(value = 0, name = "ordinary")

  final case object Special extends OfferTypeItem(value = 1, name = "special")
}
