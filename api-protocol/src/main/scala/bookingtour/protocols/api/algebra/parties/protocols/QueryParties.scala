package bookingtour.protocols.api.algebra.parties.protocols

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.types.Capture
import bookingtour.protocols.core.types.Capture._
import bookingtour.protocols.core.values.enumeration.ContextItem

/**
  * © Alexey Toroshchin 2019.
  */
trait QueryParties[+A] {
  def customers(producer: ActorRef, partyId: UUID, ctx: ContextItem): A
  def suppliers(producer: ActorRef, partyId: UUID, ctx: ContextItem): A
}

object QueryParties extends Constructors[QueryParties] {
  def customers(
      producer: ActorRef,
      partyId: UUID,
      ctx: ContextItem
  ): Capture[QueryParties] = Capture[QueryParties](_.customers(producer, partyId, ctx))

  def suppliers(
      producer: ActorRef,
      partyId: UUID,
      ctx: ContextItem
  ): Capture[QueryParties] = Capture[QueryParties](_.suppliers(producer, partyId, ctx))
}
