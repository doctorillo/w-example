package bookingtour.protocols.property.prices.api

import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookTariffId}
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.property.prices.newTypes.TariffId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.{DescriptionAPI, LabelAPI}
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class TariffOp(
    id: TariffId,
    tariffSync: LookTariffId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    labels: List[LabelAPI],
    descriptions: List[DescriptionAPI]
)

object TariffOp {
  final type Id = TariffId

  implicit final val itemR0: TariffOp => Id = _.id

  implicit final val itemP0: TariffOp => Int = _ => 0
}
