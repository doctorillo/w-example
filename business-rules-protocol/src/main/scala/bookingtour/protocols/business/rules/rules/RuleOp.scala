package bookingtour.protocols.business.rules.rules

import bookingtour.protocols.business.rules.enumeration.{
  OperatorRuleItem,
  OperatorValueItem,
  PropertyPriceTargetRuleItem
}
import bookingtour.protocols.business.rules.enumeration.{
  OperatorRuleItem,
  OperatorValueItem,
  PropertyPriceTargetRuleItem
}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
sealed trait RuleOp

object RuleOp {
  @derive(encoder, decoder, order)
  final case class PropertyOp(
      target: PropertyPriceTargetRuleItem,
      operator: OperatorRuleItem,
      valueType: OperatorValueItem,
      value: Double
  ) extends RuleOp
}
