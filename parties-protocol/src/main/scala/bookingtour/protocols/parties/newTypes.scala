package bookingtour.protocols.parties

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2020.
  */
object newTypes {
  @newtype final case class SolverId(x: UUID)
  object SolverId {
    implicit val a: UUID => SolverId = x => SolverId(x)
    implicit val b: SolverId => UUID = _.x
  }

  @newtype final case class Email(x: String)

  @newtype final case class PasswordHash(x: String)

  @newtype final case class UserId(x: UUID)
  object UserId {
    implicit val a: UUID => UserId = x => UserId(x)
    implicit val b: UserId => UUID = _.x
  }

  @newtype final case class PartyId(x: UUID)
  object PartyId {
    implicit val a: UUID => PartyId = x => PartyId(x)
    implicit val b: PartyId => UUID = _.x
  }

  @newtype final case class PersonId(x: UUID)
  object PersonId {
    implicit val a: UUID => PersonId = x => PersonId(x)
    implicit val b: PersonId => UUID = _.x
  }

  @newtype final case class CompanyId(x: UUID)
  object CompanyId {
    implicit val a: UUID => CompanyId = x => CompanyId(x)
    implicit val b: CompanyId => UUID = _.x
  }

  @newtype final case class AppId(x: UUID)
  object AppId {
    implicit val a: UUID => AppId = x => AppId(x)
    implicit val b: AppId => UUID = _.x
  }

  @newtype final case class AppLangId(x: UUID)
  object AppLangId {
    implicit val a: UUID => AppLangId = x => AppLangId(x)
    implicit val b: AppLangId => UUID = _.x
  }

  @newtype final case class AppContextId(x: UUID)
  object AppContextId {
    implicit val a: UUID => AppContextId = x => AppContextId(x)
    implicit val b: AppContextId => UUID = _.x
  }

  @newtype final case class ProviderId(x: UUID)
  object ProviderId {
    implicit val a: UUID => ProviderId = x => ProviderId(x)
    implicit val b: ProviderId => UUID = _.x
  }

  @newtype final case class SupplierGroupId(x: UUID)
  object SupplierGroupId {
    implicit val a: UUID => SupplierGroupId = x => SupplierGroupId(x)
    implicit val b: SupplierGroupId => UUID = _.x
  }

  @newtype final case class SupplierGroupMemberId(x: UUID)
  object SupplierGroupMemberId {
    implicit val a: UUID => SupplierGroupMemberId = x => SupplierGroupMemberId(x)
    implicit val b: SupplierGroupMemberId => UUID = _.x
  }

  @newtype final case class CustomerGroupId(x: UUID)
  object CustomerGroupId {
    implicit val a: UUID => CustomerGroupId = x => CustomerGroupId(x)
    implicit val b: CustomerGroupId => UUID = _.x
  }

  @newtype final case class CustomerGroupMemberId(x: UUID)
  object CustomerGroupMemberId {
    implicit val a: UUID => CustomerGroupMemberId = x => CustomerGroupMemberId(x)
    implicit val b: CustomerGroupMemberId => UUID = _.x
  }

  @newtype final case class CountryId(x: UUID)
  object CountryId {
    implicit val a: UUID => CountryId = x => CountryId(x)
    implicit val b: CountryId => UUID = _.x
  }

  @newtype final case class RegionId(x: UUID)
  object RegionId {
    implicit val a: UUID => RegionId = x => RegionId(x)
    implicit val b: RegionId => UUID = _.x
  }

  @newtype final case class CityId(x: UUID)
  object CityId {
    implicit val a: UUID => CityId = x => CityId(x)
    implicit val b: CityId => UUID = _.x
  }

  @newtype final case class CityDistrictId(x: UUID)
  object CityDistrictId {
    implicit val a: UUID => CityDistrictId = x => CityDistrictId(x)
    implicit val b: CityDistrictId => UUID = _.x
  }

  @newtype final case class PickupPointId(x: UUID)
  object PickupPointId {
    implicit val a: UUID => PickupPointId = x => PickupPointId(x)
    implicit val b: PickupPointId => UUID = _.x
  }

  @newtype final case class AddressId(x: UUID)
  object AddressId {
    implicit val a: UUID => AddressId = x => AddressId(x)
    implicit val b: AddressId => UUID = _.x
  }

  @newtype final case class PointId(x: UUID)
  object PointId {
    implicit val a: UUID => PointId = x => PointId(x)
    implicit val b: PointId => UUID = _.x
  }
}
