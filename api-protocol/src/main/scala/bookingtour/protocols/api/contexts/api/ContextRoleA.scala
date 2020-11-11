package bookingtour.protocols.api.contexts.api

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.{ContextItem, ContextRoleItem}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  * @param id dynamic if ContextRoleItem Customer or Supplier id === group_member_id
  * if ContextRoleItem === Provider id === provider id
  */
@derive(encoder, decoder, order)
final case class ContextRoleA(id: UUID, ctx: ContextItem, role: ContextRoleItem)
