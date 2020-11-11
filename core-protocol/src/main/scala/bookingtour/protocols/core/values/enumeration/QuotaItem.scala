package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable
import derevo.cats.order
import derevo.derive

/**
  * Created by d0ct0r on 30.03.2019.
  */
@derive(order, loggable)
sealed abstract class QuotaItem(val value: Int, val name: String) extends IntEnumEntry

case object QuotaItem extends IntEnum[QuotaItem] with IntCirceEnum[QuotaItem] with LoggableIntEnum[QuotaItem] {
  override def values: immutable.IndexedSeq[QuotaItem] = findValues

  final case object Undefined extends QuotaItem(value = 0, name = "undefined")

  final case object Commitment extends QuotaItem(value = 1, name = "commitment")

  final case object Allotment extends QuotaItem(value = 2, name = "allotment")

  final case object Option extends QuotaItem(value = 3, name = "option")
}
