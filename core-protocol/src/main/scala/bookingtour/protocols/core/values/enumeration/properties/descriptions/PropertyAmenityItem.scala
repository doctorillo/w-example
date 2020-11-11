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
sealed abstract class PropertyAmenityItem(val value: Int, val name: String) extends IntEnumEntry

case object PropertyAmenityItem
    extends IntEnum[PropertyAmenityItem] with IntCirceEnum[PropertyAmenityItem]
    with LoggableIntEnum[PropertyAmenityItem] {
  override def values: immutable.IndexedSeq[PropertyAmenityItem] = findValues

  case object BalneoLogicalCenter extends PropertyAmenityItem(value = 1, name = "бальнеоцентр")
  case object ThermalSource       extends PropertyAmenityItem(value = 2, name = "подведена вода из источника")
  case object SaltRoom            extends PropertyAmenityItem(value = 3, name = "соляная комната")
  case object BioLamp             extends PropertyAmenityItem(value = 4, name = "биолампа")
  case object BicyclesForRent     extends PropertyAmenityItem(value = 5, name = "прокат велосипедов")

  case object Bowling           extends PropertyAmenityItem(value = 6, name = "боулинг")
  case object RentalCar         extends PropertyAmenityItem(value = 7, name = "прокат автомобилей")
  case object Casino            extends PropertyAmenityItem(value = 8, name = "казино")
  case object ChildrenCorner    extends PropertyAmenityItem(value = 9, name = "детский уголок")
  case object Concierge         extends PropertyAmenityItem(value = 10, name = "консьерж")
  case object IndoorPool        extends PropertyAmenityItem(value = 11, name = "крытый бассейн")
  case object DryCleaning       extends PropertyAmenityItem(value = 12, name = "химчистка")
  case object Gym               extends PropertyAmenityItem(value = 13, name = "тренажерный зал")
  case object GolfLessons       extends PropertyAmenityItem(value = 14, name = "уроки игры в гольф")
  case object Barbershop        extends PropertyAmenityItem(value = 15, name = "парикмахерская")
  case object Parking           extends PropertyAmenityItem(value = 16, name = "парковка")
  case object LobbyInternet     extends PropertyAmenityItem(value = 17, name = "интернет в лобби")
  case object RoomInternet      extends PropertyAmenityItem(value = 18, name = "интернет в номере")
  case object IroningFacilities extends PropertyAmenityItem(value = 19, name = "гладильные принадлежности")
  case object Library           extends PropertyAmenityItem(value = 20, name = "библиотека")
  case object Lift              extends PropertyAmenityItem(value = 21, name = "лифт")
  case object LobbyBar          extends PropertyAmenityItem(value = 22, name = "лобби бар")
  case object MiniGolf          extends PropertyAmenityItem(value = 23, name = "минигольф")
  case object CurrencyExchange  extends PropertyAmenityItem(value = 24, name = "обмен валюты")
  case object NonSmokingRooms   extends PropertyAmenityItem(value = 25, name = "комнаты для некурящих")
  case object OpenPool          extends PropertyAmenityItem(value = 26, name = "открытый бассейн")
  case object Garage            extends PropertyAmenityItem(value = 27, name = "гараж")
  case object Restaurant        extends PropertyAmenityItem(value = 28, name = "ресторан")
  case object RoomService       extends PropertyAmenityItem(value = 29, name = "обслуживание в номере")
  case object RoomsForPeopleWithDisabilities
      extends PropertyAmenityItem(
        value = 30,
        name = "номера для людей с ограниченными возможностями"
      )

  case object SafeAtReception
      extends PropertyAmenityItem(
        value = 31,
        name = "сейф на рецепции"
      )

  case object InRoomSafe
      extends PropertyAmenityItem(
        value = 32,
        name = "сейф в номере"
      )

  case object Sauna
      extends PropertyAmenityItem(
        value = 33,
        name = "сауна"
      )

  case object Solarium
      extends PropertyAmenityItem(
        value = 34,
        name = "солярий"
      )

  case object SummerTerrace
      extends PropertyAmenityItem(
        value = 35,
        name = "летняя терраса"
      )

  case object HydroMassagePool
      extends PropertyAmenityItem(
        value = 36,
        name = "гидромассажный бассейн"
      )

  case object NonSmokingThroughout
      extends PropertyAmenityItem(
        value = 37,
        name = "курение на всей территории запрещено"
      )

  case object TreatmentChildren
      extends PropertyAmenityItem(
        value = 38,
        name = "лечение детей"
      )

  case object FrontLineLocation
      extends PropertyAmenityItem(
        value = 39,
        name = "расположение на первой линии"
      )

  case object Atm
      extends PropertyAmenityItem(
        value = 40,
        name = "банкомат"
      )

  case object Reception
      extends PropertyAmenityItem(
        value = 41,
        name = "круглосуточная стойка регистрации"
      )

  case object MultiLang
      extends PropertyAmenityItem(
        value = 42,
        name = "мультиязычный персонал"
      )

  case object Cafe
      extends PropertyAmenityItem(
        value = 43,
        name = "кафе"
      )

  case object BabySitter
      extends PropertyAmenityItem(
        value = 44,
        name = "услуги няни и уход за детьми"
      )

  case object ConferenceHall
      extends PropertyAmenityItem(
        value = 45,
        name = "конференц-зал"
      )

  case object Laundry
      extends PropertyAmenityItem(
        value = 46,
        name = "прачечная"
      )

  case object LuggageStorage
      extends PropertyAmenityItem(
        value = 47,
        name = "камера хранения"
      )

  case object Fitness
      extends PropertyAmenityItem(
        value = 48,
        name = "фитнес"
      )

  case object Spa
      extends PropertyAmenityItem(
        value = 49,
        name = "спа-услуги"
      )

  case object BusinessCenter
      extends PropertyAmenityItem(
        value = 50,
        name = "бизнес-центр"
      )

  case object Jacuzzi
      extends PropertyAmenityItem(
        value = 51,
        name = "джакузи"
      )

  case object SteamBath
      extends PropertyAmenityItem(
        value = 52,
        name = "паровая баня"
      )

  case object Garden
      extends PropertyAmenityItem(
        value = 53,
        name = "сад"
      )

  case object FamilyHoliday
      extends PropertyAmenityItem(
        value = 54,
        name = "Семейный отдых"
      )

  case object MeetingsBanquets
      extends PropertyAmenityItem(
        value = 55,
        name = "организация встреч и банкетов"
      )

  case object PackedLunches
      extends PropertyAmenityItem(
        value = 56,
        name = "упакованные ланчи"
      )

  case object BridalSuite
      extends PropertyAmenityItem(
        value = 57,
        name = "люкс для новобрачных"
      )

  case object BeautySaloon
      extends PropertyAmenityItem(
        value = 58,
        name = "салон красоты"
      )

  final val toCreateWithLabel: List[EnumProjectionE.CreateWithLabel] =
    PropertyAmenityItem.values.toList.map(x => EnumProjectionE.CreateWithLabel(valueId = x.value, name = x.name))
}
