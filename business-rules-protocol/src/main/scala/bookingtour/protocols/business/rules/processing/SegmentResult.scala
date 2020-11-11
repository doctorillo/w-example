package bookingtour.protocols.business.rules.processing

import java.time.LocalDate

import bookingtour.protocols.business.rules.enumeration.RuleContextItem
import bookingtour.protocols.business.rules.newTypes.BusinessRuleId
import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
sealed abstract class SegmentResult(
    val supplier: PartyId,
    val customer: PartyId,
    val group: CustomerGroupId,
    val rule: BusinessRuleId,
    val ctx: RuleContextItem,
    val issueDate: LocalDate
) extends Product with Serializable

object SegmentResult {
  @derive(encoder, decoder, order)
  final case class PriceSegment(
      override val supplier: PartyId,
      override val customer: PartyId,
      override val group: CustomerGroupId,
      override val rule: BusinessRuleId,
      override val ctx: RuleContextItem,
      override val issueDate: LocalDate,
      amount: Amount
  ) extends SegmentResult(supplier, customer, group, rule, ctx, issueDate)

  @derive(encoder, decoder, order)
  final case class BonusSegment(
      override val supplier: PartyId,
      override val customer: PartyId,
      override val group: CustomerGroupId,
      override val rule: BusinessRuleId,
      override val ctx: RuleContextItem,
      override val issueDate: LocalDate,
      amount: Amount
  ) extends SegmentResult(supplier, customer, group, rule, ctx, issueDate)

  @derive(encoder, decoder, order)
  final case class PaymentDateSegment(
      override val supplier: PartyId,
      override val customer: PartyId,
      override val group: CustomerGroupId,
      override val rule: BusinessRuleId,
      override val ctx: RuleContextItem,
      override val issueDate: LocalDate,
      amount: Amount
  ) extends SegmentResult(supplier, customer, group, rule, ctx, issueDate)

  @derive(encoder, decoder, order)
  final case class PenaltySegment(
      override val supplier: PartyId,
      override val customer: PartyId,
      override val group: CustomerGroupId,
      override val rule: BusinessRuleId,
      override val ctx: RuleContextItem,
      override val issueDate: LocalDate,
      amount: Amount
  ) extends SegmentResult(supplier, customer, group, rule, ctx, issueDate)
}
