package bookingtour.protocols.api.business.business

import java.util.UUID

import bookingtour.protocols.core.values.Ranges

/**
  * © Alexey Toroshchin 2019.
  */
final case class GroupRuleAGG[A <: ContextRule](
    id: UUID,
    groupId: UUID,
    sale: Option[Ranges.Dates],
    pay: Option[Ranges.Dates],
    supply: Option[Ranges.Dates],
    refundable: Boolean,
    rule: BusinessRule[A]
)
