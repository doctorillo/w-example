package bookingtour.protocols.business.rules

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2020.
  */
object newTypes {
  @newtype final case class BusinessRuleId(x: UUID)

}
