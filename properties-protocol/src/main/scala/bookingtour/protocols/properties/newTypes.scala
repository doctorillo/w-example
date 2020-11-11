package bookingtour.protocols.properties

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
 * © Alexey Toroshchin 2020.
 */
object newTypes {
  @newtype final case class PropertyId(x: UUID)
  object PropertyId {
    implicit val a: UUID => PropertyId = x => PropertyId(x)
    implicit val b: PropertyId => UUID = _.x
  }

  @newtype final case class CategoryBoardingId(x: UUID)
  object CategoryBoardingId {
    implicit val a: UUID => CategoryBoardingId = x => CategoryBoardingId(x)
    implicit val b: CategoryBoardingId => UUID = _.x
  }

  @newtype final case class BoardingId(x: UUID)
  object BoardingId {
    implicit val a: UUID => BoardingId = x => BoardingId(x)
    implicit val b: BoardingId => UUID = _.x
  }

  @newtype final case class RoomTypeId(x: UUID)
  object RoomTypeId {
    implicit val a: UUID => RoomTypeId = x => RoomTypeId(x)
    implicit val b: RoomTypeId => UUID = _.x
  }

  @newtype final case class RoomCategoryId(x: UUID)
  object RoomCategoryId {
    implicit val a: UUID => RoomCategoryId = x => RoomCategoryId(x)
    implicit val b: RoomCategoryId => UUID = _.x
  }

  @newtype final case class RoomUnitId(x: UUID)
  object RoomUnitId {
    implicit val a: UUID => RoomUnitId = x => RoomUnitId(x)
    implicit val b: RoomUnitId => UUID = _.x
  }

  @newtype final case class AmenityId(x: UUID)
  object AmenityId {
    implicit val a: UUID => AmenityId = x => AmenityId(x)
    implicit val b: AmenityId => UUID = _.x
  }

  @newtype final case class FacilityId(x: UUID)
  object FacilityId {
    implicit val a: UUID => FacilityId = x => FacilityId(x)
    implicit val b: FacilityId => UUID = _.x
  }

  @newtype final case class TherapyId(x: UUID)
  object TherapyId {
    implicit val a: UUID => TherapyId = x => TherapyId(x)
    implicit val b: TherapyId => UUID = _.x
  }

  @newtype final case class MedicalDepartmentId(x: UUID)
  object MedicalDepartmentId {
    implicit val a: UUID => MedicalDepartmentId = x => MedicalDepartmentId(x)
    implicit val b: MedicalDepartmentId => UUID = _.x
  }

  @newtype final case class TreatmentIndicationId(x: UUID)
  object TreatmentIndicationId {
    implicit val a: UUID => TreatmentIndicationId = x => TreatmentIndicationId(x)
    implicit val b: TreatmentIndicationId => UUID = _.x
  }

}
