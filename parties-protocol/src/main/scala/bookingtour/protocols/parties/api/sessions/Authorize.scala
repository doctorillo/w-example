package bookingtour.protocols.parties.api.sessions

import akka.actor.ActorRef
import bookingtour.protocols.core.newtypes.values.{UserName, UserPassword}
import bookingtour.protocols.core.sessions.{RemoteValue, SessionIdValue}

/**
  * © Alexey Toroshchin 2020.
  */
final case class Authorize(
    sessionId: SessionIdValue,
    ip: RemoteValue,
    userName: UserName,
    password: UserPassword,
    replayTo: ActorRef
)
