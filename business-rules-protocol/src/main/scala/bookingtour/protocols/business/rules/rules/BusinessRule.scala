package bookingtour.protocols.business.rules.rules

import bookingtour.protocols.business.rules.newTypes.BusinessRuleId
import bookingtour.protocols.parties.newTypes.CustomerGroupId

/**
  * © Alexey Toroshchin 2019.
  */
final case class BusinessRule[T <: ConditionProduct](
    id: BusinessRuleId,
    groupId: List[CustomerGroupId],
    condition: T,
    expr: RuleOp
)

trait RuleConsumer[T] {
  def applicable(condition: T): Option[RuleOp]
}
