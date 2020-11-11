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
sealed abstract class PointItem(val value: Int) extends IntEnumEntry

case object PointItem extends IntEnum[PointItem] with IntCirceEnum[PointItem] with LoggableIntEnum[PointItem] {
  override def values: immutable.IndexedSeq[PointItem] = findValues

  case object Undefined extends PointItem(value = 0)

  case object Country extends PointItem(value = 1)

  case object Region extends PointItem(value = 2)

  case object City extends PointItem(value = 3)

  case object Airport extends PointItem(value = 4)

  case object RailwayStation extends PointItem(value = 5)

  case object BusStation extends PointItem(value = 6)

  case object Collector extends PointItem(value = 7)

  case object AccommodationPoint extends PointItem(value = 8)

  case object Metro extends PointItem(value = 9)

  case object Tag extends PointItem(value = 10)

  final val fromInterLook: Int => PointItem = {
    case 1 =>
      Airport
    case 2 =>
      AccommodationPoint
    case 3 =>
      Collector
    case 5 =>
      RailwayStation
    case 9 =>
      BusStation
    case 11 =>
      BusStation
    case _ =>
      Undefined

  }
}
