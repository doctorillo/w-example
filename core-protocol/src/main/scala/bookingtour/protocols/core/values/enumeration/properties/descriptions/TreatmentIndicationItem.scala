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
sealed abstract class TreatmentIndicationItem(val value: Int, val name: String) extends IntEnumEntry

case object TreatmentIndicationItem
    extends IntEnum[TreatmentIndicationItem] with IntCirceEnum[TreatmentIndicationItem]
    with LoggableIntEnum[TreatmentIndicationItem] {
  override def values: immutable.IndexedSeq[TreatmentIndicationItem] = findValues

  case object DiseasesCirculatorySystem
      extends TreatmentIndicationItem(
        value = 1,
        name = "заболевания системы кровообращения, сердца и сосудов"
      )

  case object DiseasesDigestiveSystem
      extends TreatmentIndicationItem(value = 2, name = "заболевания желудочно-кишечного тракта")

  case object KidneyDiseases
      extends TreatmentIndicationItem(
        value = 3,
        name = "заболевания почек и мочевыводящих путей"
      )

  case object MedicalLaboratory
      extends TreatmentIndicationItem(
        value = 4,
        name = "медицинская лаборатория"
      )

  case object MetabolicDisorders
      extends TreatmentIndicationItem(
        value = 5,
        name = "нарушения обмена веществ"
      )

  case object MusculoSkeletalDisorders
      extends TreatmentIndicationItem(
        value = 6,
        name = "заболевания опорно-двигательного аппарата"
      )

  case object DiseasesNervousSystem
      extends TreatmentIndicationItem(
        value = 7,
        name = "болезни нервной системы"
      )

  case object NeurologicalDiseases
      extends TreatmentIndicationItem(
        value = 8,
        name = "неврологические заболевания"
      )

  case object ObesityOverweight
      extends TreatmentIndicationItem(
        value = 9,
        name = "ожирение и избыточный вес"
      )

  case object OncologicalDiseases
      extends TreatmentIndicationItem(
        value = 10,
        name = "онкологические заболевания"
      )

  case object Rehabilitation
      extends TreatmentIndicationItem(
        value = 11,
        name = "востановления после операционного вмешательства и-или посттравматического состояния"
      )

  case object RespiratoryDiseases
      extends TreatmentIndicationItem(
        value = 12,
        name = "респираторные заболевания"
      )

  case object DiabeticDisease
      extends TreatmentIndicationItem(
        value = 13,
        name = "лечение и профилактика диабетического заболевания"
      )

  /*case object DiseasesEndocrineSystem
      extends TreatmentIndicationItem(
        value = 2,
        name = "болезни эндокринной системы"
      )
  case object NutritionalDisorders
      extends TreatmentIndicationItem(
        value = 3,
        name = "расстройства питания"
      )
  case object MetabolicDisorders
      extends TreatmentIndicationItem(
        value = 4,
        name = "нарушение обмена веществ"
      )
  case object KidneyDiseases
      extends TreatmentIndicationItem(
        value = 5,
        name = "болезни почек, мочевыводящих путей"
      )
  case object MusculoSkeletalDisorders
      extends TreatmentIndicationItem(
        value = 6,
        name = "болезни костно-мышечной системы"
      )
  case object DiseasesNervousSystem
      extends TreatmentIndicationItem(
        value = 7,
        name = "болезни нервной системы"
      )
  case object DiseasesGynecological
      extends TreatmentIndicationItem(
        value = 8,
        name = "гинекологические заболевания"
      )
  case object DiseasesRespiratorySystem
      extends TreatmentIndicationItem(
        value = 10,
        name = "болезни системы кровообращения"
      )*/

  final val toCreateWithLabel: List[EnumProjectionE.CreateWithLabel] =
    TreatmentIndicationItem.values.toList.map(x => EnumProjectionE.CreateWithLabel(valueId = x.value, name = x.name))
}
