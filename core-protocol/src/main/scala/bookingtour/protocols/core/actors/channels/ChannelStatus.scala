package bookingtour.protocols.core.actors.channels

import scala.collection.immutable

import cats.{Order, Semigroup}
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelStatus(val value: Int) extends IntEnumEntry

case object ChannelStatus extends IntEnum[ChannelStatus] with IntCirceEnum[ChannelStatus] {
  override def values: immutable.IndexedSeq[ChannelStatus] = findValues

  final case object Undefined extends ChannelStatus(value = 0)

  final case object Busy extends ChannelStatus(value = 1)

  final case object Ready extends ChannelStatus(value = 2)

  final def undefined: ChannelStatus = Undefined

  final def busy: ChannelStatus = Busy

  final def ready: ChannelStatus = Ready

  implicit final val channelStatusSG: Semigroup[ChannelStatus] =
    (x: ChannelStatus, y: ChannelStatus) => {
      if (x.value <= y.value) {
        x
      } else {
        y
      }
    }

  implicit final val channelStatusO: Order[ChannelStatus] =
    (x: ChannelStatus, y: ChannelStatus) => x.value.compareTo(y.value)
}
