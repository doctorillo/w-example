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
sealed abstract class ActivityItem(val value: Int, val name: String) extends IntEnumEntry

case object ActivityItem
    extends IntEnum[ActivityItem] with IntCirceEnum[ActivityItem] with LoggableIntEnum[ActivityItem] {
  override def values: immutable.IndexedSeq[ActivityItem] = findValues

  final case object Undefined extends ActivityItem(value = 0, name = "undefined")

  final case object AccommodationPerPax extends ActivityItem(value = 1, name = "accommodation per pax")

  final case object AccommodationPerRoom extends ActivityItem(value = 2, name = "accommodation per room")

  final case object AccommodationComposed extends ActivityItem(value = 3, name = "accommodation composed")

  final case object TransferGroup extends ActivityItem(value = 4, name = "transfer group")

  final case object TransferIndividual extends ActivityItem(value = 5, name = "transfer individual")

  final case object ExcursionGroup extends ActivityItem(value = 6, name = "excursion group")

  final case object ExcursionIndividual extends ActivityItem(value = 7, name = "excursion individual")

  final case object Extra extends ActivityItem(value = 8, name = "extra")

  final case object AccommodationPenalty extends ActivityItem(value = 9, name = "accommodation penalty")

  final case object TransferPenalty extends ActivityItem(value = 10, name = "transfer penalty")

  final case object ExcursionPenalty extends ActivityItem(value = 11, name = "excursion penalty")

}
