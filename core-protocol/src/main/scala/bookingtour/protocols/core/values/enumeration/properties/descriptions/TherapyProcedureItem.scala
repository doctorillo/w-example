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
  *
  * last version
  */
@derive(order, loggable)
sealed abstract class TherapyProcedureItem(val value: Int, val name: String) extends IntEnumEntry

case object TherapyProcedureItem
    extends IntEnum[TherapyProcedureItem] with IntCirceEnum[TherapyProcedureItem]
    with LoggableIntEnum[TherapyProcedureItem] {
  override def values: immutable.IndexedSeq[TherapyProcedureItem] = findValues

  case object Acupuncture          extends TherapyProcedureItem(value = 1, name = "акупунктура")
  case object AnticelluliteMassage extends TherapyProcedureItem(value = 2, name = "антицеллюлитный массаж")
  case object AromaMassage         extends TherapyProcedureItem(value = 3, name = "арома массаж")
  case object Aerobics             extends TherapyProcedureItem(value = 4, name = "аэробика")
  case object MineralWaterPool     extends TherapyProcedureItem(value = 5, name = "бассейн с минеральной водой")
  case object BioLamp              extends TherapyProcedureItem(value = 6, name = "биолампа")
  case object Cleopatra            extends TherapyProcedureItem(value = 7, name = "Ванна Клеопатра")
  case object FootWhirlpool        extends TherapyProcedureItem(value = 8, name = "вихревая ванна для нижних конечностей")
  case object SpinalTraction       extends TherapyProcedureItem(value = 9, name = "вытяжение позвоночника")
  case object GasBag               extends TherapyProcedureItem(value = 10, name = "газовый мешок - co2")
  case object Hydroxer             extends TherapyProcedureItem(value = 11, name = "гидроксер")
  case object MudBath              extends TherapyProcedureItem(value = 12, name = "грязевая ванна")
  case object MudWarp              extends TherapyProcedureItem(value = 13, name = "грязевое обертывание")
  case object Diadynamic           extends TherapyProcedureItem(value = 14, name = "Диадинамотерапия")
  case object Diathermy            extends TherapyProcedureItem(value = 15, name = "Диатермия")
  case object Diet                 extends TherapyProcedureItem(value = 16, name = "диетотерапия")
  case object KneippPath           extends TherapyProcedureItem(value = 17, name = "Дорожка кнайпа")
  case object BreathingExercises   extends TherapyProcedureItem(value = 18, name = "дыхательная гимнастика")
  case object InhalationMineral
      extends TherapyProcedureItem(value = 19, name = "ингаляции с использование минеральной воды")
  case object Inhalation           extends TherapyProcedureItem(value = 20, name = "ингаляции")
  case object InterferenceCurrents extends TherapyProcedureItem(value = 21, name = "Интерференционные токи")
  case object Yoga                 extends TherapyProcedureItem(value = 22, name = "йога")
  case object Quartz               extends TherapyProcedureItem(value = 23, name = "кварцевая лампа")
  case object Kinesiotherapy       extends TherapyProcedureItem(value = 24, name = "Кинезиотейпинг")
  case object Oxygen               extends TherapyProcedureItem(value = 25, name = "кислородная терапия")
  case object IntestinalWash       extends TherapyProcedureItem(value = 26, name = "кишечные орошения - промывание")
  case object ClassicMassage       extends TherapyProcedureItem(value = 27, name = "классический массаж")
  case object HempBath             extends TherapyProcedureItem(value = 28, name = "Коноплянная ванна")
  case object Cryo                 extends TherapyProcedureItem(value = 29, name = "криотерапия")
  case object Laser                extends TherapyProcedureItem(value = 30, name = "лазерная терапия")
  case object PhysiotherapyPool    extends TherapyProcedureItem(value = 31, name = "Лечебная гимнастика в бассейне")
  case object Physiotherapy        extends TherapyProcedureItem(value = 32, name = "Лечебная гимнастика")
  case object LymphaticDrainage    extends TherapyProcedureItem(value = 33, name = "лимфодренаж")
  case object Magnetic             extends TherapyProcedureItem(value = 34, name = "магнитотерапия")
  case object FaceMassage          extends TherapyProcedureItem(value = 35, name = "массаж лица")
  case object HoneyWrap            extends TherapyProcedureItem(value = 36, name = "медовое обертывание")
  case object HoneyMassage         extends TherapyProcedureItem(value = 37, name = "медовый массаж")
  case object MyoStimulation       extends TherapyProcedureItem(value = 38, name = "миостимуляция")
  case object MobilizationPeripheralJoints
      extends TherapyProcedureItem(value = 39, name = "мобилизация периферических суставов")
  case object OatBath                 extends TherapyProcedureItem(value = 40, name = "Овсяная ванна")
  case object IrrigationGums          extends TherapyProcedureItem(value = 41, name = "орошение десен минеральной водой")
  case object Parafango               extends TherapyProcedureItem(value = 42, name = "парафанго - парафинотерапия")
  case object PneumoPuncture          extends TherapyProcedureItem(value = 43, name = "пневмо пунктура - газовые уколы")
  case object UnderwaterMassage       extends TherapyProcedureItem(value = 44, name = "подводный массаж")
  case object NaturalMudBath          extends TherapyProcedureItem(value = 45, name = "природная грязевая ванна")
  case object NaturalPearl            extends TherapyProcedureItem(value = 46, name = "природная жемчужная ванна")
  case object NaturalIodideBath       extends TherapyProcedureItem(value = 47, name = "природная йодобромная ванна")
  case object Sulfur                  extends TherapyProcedureItem(value = 48, name = "природная серная ванна")
  case object NaturalThermal          extends TherapyProcedureItem(value = 49, name = "природная термальная ванна")
  case object NaturalCarbonDioxid     extends TherapyProcedureItem(value = 50, name = "природная углекислая ванна")
  case object Radiotherapy            extends TherapyProcedureItem(value = 51, name = "радиотерапия")
  case object Radon                   extends TherapyProcedureItem(value = 52, name = "радоновая ванна")
  case object ReflexMassage           extends TherapyProcedureItem(value = 53, name = "рефлекторный массаж")
  case object ManualLymphaticDrainage extends TherapyProcedureItem(value = 54, name = "ручной лимфодренаж")
  case object NordicWalking           extends TherapyProcedureItem(value = 55, name = "Скандинавская ходьба")
  case object SaltCave                extends TherapyProcedureItem(value = 56, name = "соляная пещера")
  case object Stone                   extends TherapyProcedureItem(value = 57, name = "стоун-терапия лавовыми камнями")
  case object Solux                   extends TherapyProcedureItem(value = 58, name = "теплолечение инфракрасными лучами - solux")
  case object Mayer                   extends TherapyProcedureItem(value = 60, name = "терапия по методу др майера")
  case object Thermal                 extends TherapyProcedureItem(value = 61, name = "термальный бассейн")
  case object Peat                    extends TherapyProcedureItem(value = 62, name = "торфяная ванна")
  case object Herbal                  extends TherapyProcedureItem(value = 63, name = "травяная ванна")
  case object CarbonDioxide           extends TherapyProcedureItem(value = 64, name = "Углекислая ванна")
  case object ShockWave               extends TherapyProcedureItem(value = 65, name = "Ударно-волновая терапия")
  case object Ultrasound              extends TherapyProcedureItem(value = 66, name = "ультразвук")
  case object PhysioPool              extends TherapyProcedureItem(value = 67, name = "физиотерапия в бассейне")
  case object Physio                  extends TherapyProcedureItem(value = 68, name = "физиотерапия")
  case object CharcotDouche           extends TherapyProcedureItem(value = 69, name = "шотландский душ - душ шарко")
  case object Ecg                     extends TherapyProcedureItem(value = 70, name = "экг - электрокардиограмма")
  case object PhyAction               extends TherapyProcedureItem(value = 71, name = "электролечение аппаратом phyaction")
  case object ElectroTherapy          extends TherapyProcedureItem(value = 72, name = "Электротерапия")
  case object EndoVaco                extends TherapyProcedureItem(value = 73, name = "эндо-вако")

  final val toCreateWithLabel: List[EnumProjectionE.CreateWithLabel] =
    TherapyProcedureItem.values.toList.map(x => EnumProjectionE.CreateWithLabel(valueId = x.value, name = x.name))
}
