package bookingtour.protocols.excursions

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2020.
  */
object newTypes {
  @newtype final case class ExcursionId(x: UUID)
  object ExcursionId {
    implicit val a: UUID => ExcursionId = x => ExcursionId(x)
    implicit val b: ExcursionId => UUID = _.x
  }

  @newtype final case class ExcursionTagId(x: UUID)
  object ExcursionTagId {
    implicit val a: UUID => ExcursionTagId = x => ExcursionTagId(x)
    implicit val b: ExcursionTagId => UUID = _.x
  }

  @newtype final case class ExcursionPackageId(x: UUID)
  object ExcursionPackageId {
    implicit val a: UUID => ExcursionPackageId = x => ExcursionPackageId(x)
    implicit val b: ExcursionPackageId => UUID = _.x
  }

  @newtype final case class ExcursionProviderId(x: UUID)
  object ExcursionProviderId {
    implicit val a: UUID => ExcursionProviderId = x => ExcursionProviderId(x)
    implicit val b: ExcursionProviderId => UUID = _.x
  }

  @newtype final case class ExcursionClientId(x: UUID)
  object ExcursionClientId {
    implicit val a: UUID => ExcursionClientId = x => ExcursionClientId(x)
    implicit val b: ExcursionClientId => UUID = _.x
  }

  @newtype final case class ExcursionProviderOfferId(x: UUID)
  object ExcursionProviderOfferId {
    implicit val a: UUID => ExcursionProviderOfferId = x => ExcursionProviderOfferId(x)
    implicit val b: ExcursionProviderOfferId => UUID = _.x
  }

  @newtype final case class ExcursionOfferDateId(x: UUID)
  object ExcursionOfferDateId {
    implicit val a: UUID => ExcursionOfferDateId = x => ExcursionOfferDateId(x)
    implicit val b: ExcursionOfferDateId => UUID = _.x
  }

  @newtype final case class ExcursionOfferId(x: UUID)
  object ExcursionOfferId {
    implicit val a: UUID => ExcursionOfferId = x => ExcursionOfferId(x)
    implicit val b: ExcursionOfferId => UUID = _.x
  }

  @newtype final case class ExcursionAttachedPackageId(x: UUID)
  object ExcursionAttachedPackageId {
    implicit val a: UUID => ExcursionAttachedPackageId = x => ExcursionAttachedPackageId(x)
    implicit val b: ExcursionAttachedPackageId => UUID = _.x
  }

  @newtype final case class ExcursionPriceId(x: UUID)
  object ExcursionPriceId {
    implicit val a: UUID => ExcursionPriceId = x => ExcursionPriceId(x)
    implicit val b: ExcursionPriceId => UUID = _.x
  }

  @newtype final case class ExcursionPackagePriceId(x: UUID)
  object ExcursionPackagePriceId {
    implicit val a: UUID => ExcursionPackagePriceId = x => ExcursionPackagePriceId(x)
    implicit val b: ExcursionPackagePriceId => UUID = _.x
  }
}
