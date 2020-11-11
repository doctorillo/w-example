package bookingtour.protocols.api.algebra.parties.protocols

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.types.Capture
import bookingtour.protocols.core.types.Capture.Constructors
import cats.data.Chain

/**
  * © Alexey Toroshchin 2019.
  */
trait QueryCompanies[+A] {
  def filtered(producer: ActorRef, partyIndents: Chain[UUID]): A
}

object QueryCompanies extends Constructors[QueryCompanies] {
  def filtered(producer: ActorRef, partyIndents: Chain[UUID]): Capture[QueryCompanies] =
    Capture[QueryCompanies](_.filtered(producer, partyIndents))
}
