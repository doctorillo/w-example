package bookingtour.protocols.business.rules.rules

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Days, PropertyStar}
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.parties.newTypes.PointId
import bookingtour.protocols.properties.newTypes.{BoardingId, CategoryBoardingId, PropertyId, RoomUnitId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
sealed abstract class ConditionProduct extends Product with Serializable

object ConditionProduct {
  def app[A <: ConditionProduct, B](
      condition: A
  )(item: B)(implicit fn: (A, B) => Boolean): Boolean =
    fn(condition, item)

  @derive(encoder, decoder, order)
  final case class OperationDatesProduct(
      context: RuleConditionState[ContextItem],
      delivery: RuleConditionState[Ranges.Dates],
      operation: RuleConditionState[Ranges.Dates],
      stayDuration: RuleConditionState[Ranges.Ints]
  )

  @derive(encoder, decoder, order)
  final case class PropertyProduct(
      properties: RuleConditionState[PropertyId],
      points: RuleConditionState[PointId],
      stars: RuleConditionState[PropertyStar],
      roomUnits: RuleConditionState[RoomUnitId],
      boardingCategories: RuleConditionState[CategoryBoardingId],
      boarding: RuleConditionState[BoardingId]
  ) extends ConditionProduct

  @derive(encoder, decoder, order)
  final case class PaymentProduct(
      context: RuleConditionState[ContextItem],
      dayAfterSale: RuleConditionState[Days],
      dayBeforeDelivery: RuleConditionState[Days],
      refundable: RuleConditionState[Boolean]
  ) extends ConditionProduct

  @derive(encoder, decoder, order)
  final case class PenaltyProduct(
      context: RuleConditionState[ContextItem],
      cancelBeforeDay: RuleConditionState[Days]
  ) extends ConditionProduct
}
