package bookingtour.protocols.interlook.source

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2020.
  */
object newTypes {

  @newtype @derive(order)
  final case class LookPartyId(x: Int)
  object LookPartyId {
    implicit val a: Int => LookPartyId = x => LookPartyId(x)
    implicit val b: LookPartyId => Int = _.x
  }

  @newtype @derive(order)
  final case class LookCustomerGroupId(x: Int)
  object LookCustomerGroupId {
    implicit val a: Int => LookCustomerGroupId = x => LookCustomerGroupId(x)
    implicit val b: LookCustomerGroupId => Int = _.x
  }

  @newtype @derive(order)
  final case class LookSolverId(x: Int)
  object LookSolverId {
    implicit val a: Int => LookSolverId = x => LookSolverId(x)
    implicit val b: LookSolverId => Int = _.x
  }

  @newtype @derive(order)
  final case class LookCountryId(x: Int)
  object LookCountryId {
    implicit val a: Int => LookCountryId = x => LookCountryId(x)
    implicit val b: LookCountryId => Int = _.x
  }

  @newtype @derive(order)
  final case class LookRegionId(x: Int)
  object LookRegionId {
    implicit val a: Int => LookRegionId = x => LookRegionId(x)
    implicit val b: LookRegionId => Int = _.x
  }

  @newtype @derive(order) final case class LookCityId(x: Int)
  object LookCityId {
    implicit val a: Int => LookCityId = x => LookCityId(x)
    implicit val b: LookCityId => Int = _.x
  }

  @newtype @derive(order) final case class LookPickupPointType(x: Int)
  object LookPickupPointType {
    implicit val a: Int => LookPickupPointType = x => LookPickupPointType(x)
    implicit val b: LookPickupPointType => Int = _.x
  }

  @newtype @derive(order) final case class LookPickupPointId(x: Int)
  object LookPickupPointId {
    implicit val a: Int => LookPickupPointId = x => LookPickupPointId(x)
    implicit val b: LookPickupPointId => Int = _.x
  }

  @newtype @derive(order) final case class LookCostId(x: Int)
  object LookCostId {
    implicit val a: Int => LookCostId = x => LookCostId(x)
    implicit val b: LookCostId => Int = _.x
  }

  @newtype @derive(order) final case class LookPriceId(x: Int)
  object LookPriceId {
    implicit val a: Int => LookPriceId = x => LookPriceId(x)
    implicit val b: LookPriceId => Int = _.x
  }

  /*@newtype final case class LookPropertyId(x: Int)
  object LookPropertyId {
    implicit val a: Int => LookPropertyId = x => LookPropertyId(x)
    implicit val b: LookPropertyId => Int = _.x
  }*/

  @newtype @derive(order) final case class LookPropertyStarId(x: Int)
  object LookPropertyStarId {
    implicit val a: Int => LookPropertyStarId = x => LookPropertyStarId(x)
    implicit val b: LookPropertyStarId => Int = _.x
  }

  /*@newtype final case class LookProviderId(x: Int)
  object LookProviderId {
    implicit val a: Int => LookProviderId = x => LookProviderId(x)
    implicit val b: LookProviderId => Int = _.x
  }*/

  @newtype @derive(order) final case class LookCostTypeId(x: Int)
  object LookCostTypeId {
    implicit val a: Int => LookCostTypeId = x => LookCostTypeId(x)
    implicit val b: LookCostTypeId => Int = _.x
  }

  @newtype @derive(order) final case class LookBoardingCategoryId(x: Int)
  object LookBoardingCategoryId {
    implicit val a: Int => LookBoardingCategoryId = x => LookBoardingCategoryId(x)
    implicit val b: LookBoardingCategoryId => Int = _.x
  }

  @newtype @derive(order) final case class LookBoardingId(x: Int)
  object LookBoardingId {
    implicit val a: Int => LookBoardingId = x => LookBoardingId(x)
    implicit val b: LookBoardingId => Int = _.x
  }

  @newtype @derive(order) final case class LookRoomTypeId(x: Int)
  object LookRoomTypeId {
    implicit val a: Int => LookRoomTypeId = x => LookRoomTypeId(x)
    implicit val b: LookRoomTypeId => Int = _.x
  }

  @newtype @derive(order) final case class LookRoomCategoryId(x: Int)
  object LookRoomCategoryId {
    implicit val a: Int => LookRoomCategoryId = x => LookRoomCategoryId(x)
    implicit val b: LookRoomCategoryId => Int = _.x
  }

  @newtype @derive(order) final case class LookAccommodationId(x: Int)
  object LookAccommodationId {
    implicit val a: Int => LookAccommodationId = x => LookAccommodationId(x)
    implicit val b: LookAccommodationId => Int = _.x
  }

  @newtype @derive(order) final case class LookAccommodationAgeId(x: Int)
  object LookAccommodationAgeId {
    implicit val a: Int => LookAccommodationAgeId = x => LookAccommodationAgeId(x)
    implicit val b: LookAccommodationAgeId => Int = _.x
  }

  @newtype @derive(order) final case class LookServiceCombinationId(x: Int)
  object LookServiceCombinationId {
    implicit val a: Int => LookServiceCombinationId = x => LookServiceCombinationId(x)
    implicit val b: LookServiceCombinationId => Int = _.x
  }

  @newtype @derive(order) final case class LookOfferId(x: Int)
  object LookOfferId {
    implicit val a: Int => LookOfferId = x => LookOfferId(x)
    implicit val b: LookOfferId => Int = _.x
  }

  @newtype @derive(order) final case class LookOfferRuleId(x: Int)
  object LookOfferRuleId {
    implicit val a: Int => LookOfferRuleId = x => LookOfferRuleId(x)
    implicit val b: LookOfferRuleId => Int = _.x
  }

  @newtype @derive(order) final case class LookTariffId(x: Int)
  object LookTariffId {
    implicit val a: Int => LookTariffId = x => LookTariffId(x)
    implicit val b: LookTariffId => Int = _.x
  }

  @newtype @derive(order) final case class LookLinkServiceId(x: Int)
  object LookLinkServiceId {
    implicit val a: Int => LookLinkServiceId = x => LookLinkServiceId(x)
    implicit val b: LookLinkServiceId => Int = _.x
  }

  @newtype @derive(order) final case class LookLinkServiceRef(x: Int)
  object LookLinkServiceRef {
    implicit val a: Int => LookLinkServiceRef = x => LookLinkServiceRef(x)
    implicit val b: LookLinkServiceRef => Int = _.x
  }

  @newtype @derive(order) final case class LookStopSaleId(x: Int)
  object LookStopSaleId {
    implicit val a: Int => LookStopSaleId = x => LookStopSaleId(x)
    implicit val b: LookStopSaleId => Int = _.x
  }

  @newtype @derive(order) final case class LookStopAllRoomTypes(x: Boolean)
  object LookStopAllRoomTypes {
    implicit val a: Boolean => LookStopAllRoomTypes = x => LookStopAllRoomTypes(x)
    implicit val b: LookStopAllRoomTypes => Boolean = _.x
  }

  @newtype @derive(order) final case class LookStopAllRoomCategories(x: Boolean)
  object LookStopAllRoomCategories {
    implicit val a: Boolean => LookStopAllRoomCategories = x => LookStopAllRoomCategories(x)
    implicit val b: LookStopAllRoomCategories => Boolean = _.x
  }

  @newtype @derive(order) final case class LookStopCanceled(x: Boolean)
  object LookStopCanceled {
    implicit val a: Boolean => LookStopCanceled = x => LookStopCanceled(x)
    implicit val b: LookStopCanceled => Boolean = _.x
  }

  @newtype @derive(order) final case class LookExcursionId(x: Int)
  object LookExcursionId {
    implicit val a: Int => LookExcursionId = x => LookExcursionId(x)
    implicit val b: LookExcursionId => Int = _.x
  }

}
