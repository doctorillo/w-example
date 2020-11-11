package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.RuleModifier
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.RuleApplyItem
import bookingtour.protocols.doobie.types.JsonbToJson
import bookingtour.protocols.interlook.source.newTypes.{LookOfferId, LookOfferRuleId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.util.{Get, Put}
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class OfferRuleEP(
    id: LookOfferRuleId,
    offerId: LookOfferId,
    input: Ranges.Ints,
    isMinusModifier: Boolean,
    modifier: RuleModifier,
    applyTo: RuleApplyItem
)

object OfferRuleEP {
  type Id = LookOfferRuleId

  implicit final val itemR: OfferRuleEP => Id = _.id

  implicit final val itemP: OfferRuleEP => Int = _ => 0

  implicit final def offerRuleEPG: Get[OfferRuleEP] = JsonbToJson.classGet[OfferRuleEP]
  implicit final def offerRuleEPP: Put[OfferRuleEP] = JsonbToJson.classPut[OfferRuleEP]

  final case class Output(
      id: Int,
      offerId: Int,
      inputFrom: Int,
      inputTo: Int,
      isMinusModifier: String,
      modifier: Int,
      applyTo: Int
  )

  implicit final val outputTransform: Output => OfferRuleEP = _.into[OfferRuleEP]
    .withFieldComputed(_.id, x => LookOfferRuleId(x.id))
    .withFieldComputed(_.offerId, x => LookOfferId(x.offerId))
    .withFieldComputed(_.input, x => Ranges.Ints(x.inputFrom, x.inputTo))
    .withFieldComputed(_.isMinusModifier, _.isMinusModifier.equals("-"))
    .withFieldComputed(_.modifier, x => RuleModifier(x.modifier))
    .withFieldComputed(_.applyTo, x => RuleApplyItem.withValue(x.applyTo))
    .transform
}
