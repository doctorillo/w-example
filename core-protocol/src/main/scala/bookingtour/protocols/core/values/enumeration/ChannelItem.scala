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
sealed abstract class ChannelItem(val value: Int, val name: String) extends IntEnumEntry

case object ChannelItem extends IntEnum[ChannelItem] with IntCirceEnum[ChannelItem] with LoggableIntEnum[ChannelItem] {
  override def values: immutable.IndexedSeq[ChannelItem] = findValues

  case object Undefined extends ChannelItem(value = -1, name = "undefined")

  case object Partner extends ChannelItem(value = 0, name = "partner")

  case object Direct extends ChannelItem(value = 1, name = "direct")

  case object External extends ChannelItem(value = 2, name = "external")

  case object PartnerReplace extends ChannelItem(value = 300, name = "partner replace")

  case object DirectReplace extends ChannelItem(value = 301, name = "direct replace")

  case object ExternalReplace extends ChannelItem(value = 302, name = "external replace")

  case object PartnerChangeDates extends ChannelItem(value = 500, name = "partner change dates")

  case object DirectChangeDates extends ChannelItem(value = 501, name = "direct change dates")

  case object ExternalChangeDates extends ChannelItem(value = 502, name = "external change dates")
}
