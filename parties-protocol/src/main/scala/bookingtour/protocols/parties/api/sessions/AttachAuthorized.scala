package bookingtour.protocols.parties.api.sessions

import akka.actor.ActorRef
import bookingtour.protocols.core.sessions.SessionIdValue
import bookingtour.protocols.parties.api.ui.ContextEnvUI

/**
  * © Alexey Toroshchin 2020.
  */
final case class AttachAuthorized(
    sessionId: SessionIdValue,
    env: ContextEnvUI,
    replayTo: ActorRef
)
