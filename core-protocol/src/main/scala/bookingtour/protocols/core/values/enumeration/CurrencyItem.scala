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
sealed abstract class CurrencyItem(val value: Int, val name: String, val sign: String) extends IntEnumEntry {
  final def toDbString: String = name.toUpperCase
}

case object CurrencyItem
    extends IntEnum[CurrencyItem] with IntCirceEnum[CurrencyItem] with LoggableIntEnum[CurrencyItem] {
  override def values: immutable.IndexedSeq[CurrencyItem] = findValues

  case object Euro extends CurrencyItem(value = 1, name = "eu", sign = "€")

  case object Czk extends CurrencyItem(value = 2, name = "czk", sign = "Kč")

  def fromString(x: String): CurrencyItem = x match {
    case "EU"  => Euro
    case "CZK" => Czk
  }
}
