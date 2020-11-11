package bookingtour.protocols.excursions.values

import java.time.LocalTime

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Hour, WeekDay}
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.excursions.newTypes.{
  ExcursionId,
  ExcursionOfferDateId,
  ExcursionOfferId,
  ExcursionProviderId,
  ExcursionProviderOfferId
}
import bookingtour.protocols.interlook.source.newTypes.{LookExcursionId, LookOfferId, LookPartyId}
import bookingtour.protocols.parties.newTypes.{CityId, PartyId, PickupPointId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionOfferProduct(
    id: ExcursionOfferId,
    provider: ExcursionProviderId,
    providerParty: PartyId,
    providerSync: LookPartyId,
    offer: ExcursionProviderOfferId,
    offerSync: LookOfferId,
    offerDate: ExcursionOfferDateId,
    excursion: ExcursionId,
    excursionSync: LookExcursionId,
    city: CityId,
    pickupPoint: PickupPointId,
    pointNames: List[LabelAPI],
    dates: Ranges.Dates,
    startTime: LocalTime,
    duration: Hour,
    days: List[WeekDay]
)

object ExcursionOfferProduct {
  final type Id = ExcursionOfferId

  implicit final val itemR: ExcursionOfferProduct => Id = _.id

  implicit final val itemP: ExcursionOfferProduct => Int = _ => 0
}
