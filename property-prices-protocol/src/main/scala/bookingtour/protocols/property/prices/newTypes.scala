package bookingtour.protocols.property.prices

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
 * © Alexey Toroshchin 2020.
 */
object newTypes {

  @newtype final case class PropertyProviderId(x: UUID)
  object PropertyProviderId {
    implicit val a: UUID => PropertyProviderId = x => PropertyProviderId(x)
    implicit val b: PropertyProviderId => UUID = _.x
  }

  @newtype final case class AccommodationId(x: UUID)
  object AccommodationId {
    implicit val a: UUID => AccommodationId = x => AccommodationId(x)
    implicit val b: AccommodationId => UUID = _.x
  }

  @newtype final case class AccommodationGuestId(x: UUID)
  object AccommodationGuestId {
    implicit val a: UUID => AccommodationGuestId = x => AccommodationGuestId(x)
    implicit val b: AccommodationGuestId => UUID = _.x
  }

  @newtype final case class AccommodationSyncId(x: UUID)
  object AccommodationSyncId {
    implicit val a: UUID => AccommodationSyncId = x => AccommodationSyncId(x)
    implicit val b: AccommodationSyncId => UUID = _.x
  }

  @newtype final case class GuestId(x: UUID)
  object GuestId {
    implicit val a: UUID => GuestId = x => GuestId(x)
    implicit val b: GuestId => UUID = _.x
  }

  @newtype final case class OfferId(x: UUID)
  object OfferId {
    implicit val a: UUID => OfferId = x => OfferId(x)
    implicit val b: OfferId => UUID = _.x
  }

  @newtype final case class TariffId(x: UUID)
  object TariffId {
    implicit val a: UUID => TariffId = x => TariffId(x)
    implicit val b: TariffId => UUID = _.x
  }

  @newtype final case class VariantId(x: UUID)
  object VariantId {
    implicit val a: UUID => VariantId = x => VariantId(x)
    implicit val b: VariantId => UUID = _.x
  }

  /*@newtype final case class VariantGuestId(x: UUID)
  object VariantGuestId {
    implicit val a: UUID => VariantGuestId = x => VariantGuestId(x)
    implicit val b: VariantGuestId => UUID = _.x
  }*/

  @newtype final case class OfferRuleId(x: UUID)
  object OfferRuleId {
    implicit val a: UUID => OfferRuleId = x => OfferRuleId(x)
    implicit val b: OfferRuleId => UUID = _.x
  }

  @newtype final case class PriceUnitId(x: UUID)
  object PriceUnitId {
    implicit val a: UUID => PriceUnitId = x => PriceUnitId(x)
    implicit val b: PriceUnitId => UUID = _.x
  }

  @newtype final case class OfferDateId(x: UUID)
  object OfferDateId {
    implicit val a: UUID => OfferDateId = x => OfferDateId(x)
    implicit val b: OfferDateId => UUID = _.x
  }

  @newtype final case class CostId(x: UUID)
  object CostId {
    implicit val a: UUID => CostId = x => CostId(x)
    implicit val b: CostId => UUID = _.x
  }

  @newtype final case class CostSyncId(x: UUID)
  object CostSyncId {
    implicit val a: UUID => CostSyncId = x => CostSyncId(x)
    implicit val b: CostSyncId => UUID = _.x
  }

  @newtype final case class PriceId(x: UUID)
  object PriceId {
    implicit val a: UUID => PriceId = x => PriceId(x)
    implicit val b: PriceId => UUID = _.x
  }

  @newtype final case class PriceSyncId(x: UUID)
  object PriceSyncId {
    implicit val a: UUID => PriceSyncId = x => PriceSyncId(x)
    implicit val b: PriceSyncId => UUID = _.x
  }
}
