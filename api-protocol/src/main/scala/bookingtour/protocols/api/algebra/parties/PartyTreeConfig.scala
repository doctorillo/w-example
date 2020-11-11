package bookingtour.protocols.api.algebra.parties

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
final case class PartyTreeConfig(
    partyTree: ActorRef,
    timeoutSec: Long
)
