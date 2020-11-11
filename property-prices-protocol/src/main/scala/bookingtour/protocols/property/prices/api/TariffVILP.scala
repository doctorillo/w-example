package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.api.{DescriptionAPI, LabelAPI}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.newTypes.{PartyId}
import bookingtour.protocols.property.prices.newTypes.TariffId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class TariffVILP(
    id: TariffId,
    supplier: PartyId,
    code: String,
    syncs: List[SyncItem],
    labels: List[LabelAPI],
    descriptions: List[DescriptionAPI],
    online: Boolean
)

object TariffVILP {
  type Id = TariffId

  implicit val itemR: TariffVILP => Id = _.id

  implicit final val itemP: TariffVILP => Int = _ => 0
}
