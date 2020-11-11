package bookingtour.protocols.excursions.values

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.excursions.newTypes.{ExcursionClientId, ExcursionOfferId, ExcursionPriceId}
import bookingtour.protocols.parties.newTypes.CustomerGroupId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionPriceProduct(
    id: ExcursionPriceId,
    excursion: ExcursionOfferId,
    groupId: CustomerGroupId,
    clientId: ExcursionClientId,
    age: Ranges.Ints,
    amount: Amount
)

object ExcursionPriceProduct {
  final type Id = ExcursionPriceId

  implicit final val itemR: ExcursionPriceProduct => Id = _.id

  implicit final val itemP: ExcursionPriceProduct => CustomerGroupId = _.groupId
}
