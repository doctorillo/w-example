package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.RuleModifier
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.RuleApplyItem
import bookingtour.protocols.interlook.source.newTypes.LookOfferRuleId
import bookingtour.protocols.property.prices.newTypes.{OfferId, OfferRuleId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order, loggable)
final case class OfferRuleVILP(
    id: OfferRuleId,
    offer: OfferId,
    sync: LookOfferRuleId,
    input: Ranges.Ints,
    isMinusModifier: Boolean,
    modifier: RuleModifier,
    applyTo: RuleApplyItem
)

object OfferRuleVILP {
  type Id = OfferRuleId

  implicit final val itemR0: OfferRuleVILP => Id = _.id
}
