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
sealed abstract class OperationChannelItem(val value: Int, val name: String) extends IntEnumEntry

case object OperationChannelItem
    extends IntEnum[OperationChannelItem] with IntCirceEnum[OperationChannelItem]
    with LoggableIntEnum[OperationChannelItem] {
  override def values: immutable.IndexedSeq[OperationChannelItem] = findValues

  case object Partner extends OperationChannelItem(value = 0, name = "partner")

  case object Direct extends OperationChannelItem(value = 1, name = "direct")

  case object External extends OperationChannelItem(value = 2, name = "external")

  case object Replace extends OperationChannelItem(value = 3, name = "replace")

  case object ChangeDates extends OperationChannelItem(value = 5, name = "change dates")

  final object syntax {
    implicit final class OperationChannelItemOps(
        private val self: OperationChannelItem
    ) extends AnyVal {
      def toChannel(parent: OperationChannelItem): ChannelItem = self match {
        case Partner =>
          ChannelItem.Partner

        case Direct =>
          ChannelItem.Direct

        case External =>
          ChannelItem.External

        case Replace =>
          parent match {
            case Partner =>
              ChannelItem.PartnerReplace

            case Direct =>
              ChannelItem.DirectReplace

            case External =>
              ChannelItem.ExternalReplace

            case ChangeDates =>
              ChannelItem.Undefined

            case Replace =>
              ChannelItem.Undefined
          }

        case ChangeDates =>
          parent match {
            case Partner =>
              ChannelItem.PartnerChangeDates

            case Direct =>
              ChannelItem.DirectChangeDates

            case External =>
              ChannelItem.ExternalChangeDates

            case ChangeDates =>
              ChannelItem.Undefined

            case Replace =>
              ChannelItem.Undefined
          }
      }
    }
  }
}
