package bookingtour.protocols.api.contexts.api

import bookingtour.protocols.core.values.enumeration.ContextItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ContextWorkspaceA(ctx: ContextItem, parties: List[ContextPartyA])
