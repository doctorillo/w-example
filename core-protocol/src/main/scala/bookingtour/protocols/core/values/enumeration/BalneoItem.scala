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
sealed abstract class BalneoItem(val value: Int, val name: String) extends IntEnumEntry

case object BalneoItem extends IntEnum[BalneoItem] with IntCirceEnum[BalneoItem] with LoggableIntEnum[BalneoItem] {
  override def values: immutable.IndexedSeq[BalneoItem] = findValues

  case object Pearl                  extends BalneoItem(value = 1, name = "жемчужная")
  case object Mineral                extends BalneoItem(value = 2, name = "минеральная")
  case object Herbal                 extends BalneoItem(value = 3, name = "с травяными добавками")
  case object Sulfur                 extends BalneoItem(value = 4, name = "серная")
  case object Thermal                extends BalneoItem(value = 5, name = "термальная")
  case object CarbonDioxide          extends BalneoItem(value = 6, name = "углекислая")
  case object FootWhirlpool          extends BalneoItem(value = 7, name = "вихревая для ног")
  case object HandsWhirlpool         extends BalneoItem(value = 8, name = "вихревая для рук")
  case object FourChamberGalvanic    extends BalneoItem(value = 9, name = "гальваническая четырехкамерная")
  case object Hydroxer               extends BalneoItem(value = 10, name = "гидроксер")
  case object Mud                    extends BalneoItem(value = 11, name = "грязевая ")
  case object DryCarbonic            extends BalneoItem(value = 12, name = "сухая углекислая")
  case object HydrogenSulfideMineral extends BalneoItem(value = 13, name = "сероводородная минеральная")
  case object Bromine                extends BalneoItem(value = 14, name = "йодобромная")
  case object Humic                  extends BalneoItem(value = 15, name = "гуминовая")
  case object KneippPath             extends BalneoItem(value = 16, name = "дорожка кнейпа")
  case object CharcotDouche          extends BalneoItem(value = 17, name = "душ шарко")
  case object IrrigationGums         extends BalneoItem(value = 18, name = "орошение десен")
  case object MudApplications        extends BalneoItem(value = 19, name = "грязевые аппликации")
  case object MudPacks               extends BalneoItem(value = 20, name = "грязевые пакеты")
}
