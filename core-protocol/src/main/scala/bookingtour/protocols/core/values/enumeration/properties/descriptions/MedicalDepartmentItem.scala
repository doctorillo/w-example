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
sealed abstract class MedicalDepartmentItem(val value: Int, val name: String) extends IntEnumEntry

case object MedicalDepartmentItem
    extends IntEnum[MedicalDepartmentItem] with IntCirceEnum[MedicalDepartmentItem]
    with LoggableIntEnum[MedicalDepartmentItem] {
  override def values: immutable.IndexedSeq[MedicalDepartmentItem] = findValues

  case object Nutritionist extends MedicalDepartmentItem(value = 1, name = "диетолог")
  case object Physio       extends MedicalDepartmentItem(value = 2, name = "физиотерапевт")
  case object Pediatrician extends MedicalDepartmentItem(value = 3, name = "педиатр")

  final val toCreateWithLabel: List[EnumProjectionE.CreateWithLabel] =
    MedicalDepartmentItem.values.toList.map(x => EnumProjectionE.CreateWithLabel(valueId = x.value, name = x.name))
}
