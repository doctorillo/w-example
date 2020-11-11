package bookingtour.protocols.core.values.enumeration.properties.descriptions

import scala.collection.immutable

import bookingtour.protocols.core.values.db.EnumProjectionE
import bookingtour.protocols.core.values.enumeration.LoggableIntEnum
import derevo.cats.order
import derevo.derive
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class RoomFacilityItem(val value: Int, val name: String) extends IntEnumEntry

case object RoomFacilityItem
    extends IntEnum[RoomFacilityItem] with IntCirceEnum[RoomFacilityItem] with LoggableIntEnum[RoomFacilityItem] {
  override def values: immutable.IndexedSeq[RoomFacilityItem] = findValues

  case object WiFi extends RoomFacilityItem(value = 1, name = "wi-fi")

  case object WiredInternet extends RoomFacilityItem(value = 2, name = "интернет проводной")

  case object AirConditioning extends RoomFacilityItem(value = 3, name = "кондиционер")

  case object WoodenFloor extends RoomFacilityItem(value = 4, name = "деревянный пол")

  case object Carpeting extends RoomFacilityItem(value = 5, name = "ковровое покрытие")

  case object Bidet extends RoomFacilityItem(value = 6, name = "биде")

  case object ShowerBath extends RoomFacilityItem(value = 7, name = "душевая кабина")

  case object BathTub extends RoomFacilityItem(value = 8, name = "ванна")

  case object HairDryer extends RoomFacilityItem(value = 9, name = "фен")

  case object BathRobe extends RoomFacilityItem(value = 10, name = "халат")

  case object Balcony extends RoomFacilityItem(value = 11, name = "балкон")

  case object Pot extends RoomFacilityItem(value = 12, name = "чайник")

  case object MiniBar extends RoomFacilityItem(value = 13, name = "мини-бар")

  case object PetsAllowed extends RoomFacilityItem(value = 14, name = "разрешены домашние животные")

  case object ForestView extends RoomFacilityItem(value = 15, name = "вид на лес")

  case object GardenView extends RoomFacilityItem(value = 16, name = "вид на сад")

  case object StreetView extends RoomFacilityItem(value = 17, name = "вид на улицу")

  case object YardView extends RoomFacilityItem(value = 18, name = "вид во двор")

  case object MountainView extends RoomFacilityItem(value = 19, name = "вид на горы")

  case object Slippers extends RoomFacilityItem(value = 20, name = "тапочки")

  case object Tv extends RoomFacilityItem(value = 21, name = "телевизор")

  case object Safe extends RoomFacilityItem(value = 22, name = "сейф")

  case object Phone extends RoomFacilityItem(value = 23, name = "телефон")

  case object FrenchBed extends RoomFacilityItem(value = 24, name = "french bed")

  case object TweenBed extends RoomFacilityItem(value = 25, name = "twin bed")

  case object TeaSet extends RoomFacilityItem(value = 26, name = "чайный набор")

  case object SatelliteTV extends RoomFacilityItem(value = 27, name = "спутниковые тв каналы")

  case object AdjacentRooms     extends RoomFacilityItem(value = 28, name = "смежные номера")
  case object FullSizeBed       extends RoomFacilityItem(value = 29, name = "двуспальная кровать full size")
  case object QueenSizeBed      extends RoomFacilityItem(value = 30, name = "двуспальная кровать queen size")
  case object KingSizeBed       extends RoomFacilityItem(value = 31, name = "двуспальная кровать king size")
  case object ToiletAccessories extends RoomFacilityItem(value = 32, name = "туалетные принадлежности")

  final val toCreateWithLabel: List[EnumProjectionE.CreateWithLabel] =
    RoomFacilityItem.values.toList.map(x => EnumProjectionE.CreateWithLabel(valueId = x.value, name = x.name))
}
