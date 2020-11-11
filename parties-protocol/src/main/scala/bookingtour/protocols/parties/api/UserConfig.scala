package bookingtour.protocols.parties.api

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.RoleItem
import cats.data.NonEmptyChain

/**
  * © Alexey Toroshchin 2019.
  */
final case class UserConfig(
    company: String,
    provider: Option[UUID],
    roles: NonEmptyChain[RoleItem],
    lookId: Option[Int]
)
