package bookingtour.protocols.parties.api.sessions

import akka.actor.ActorRef
import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue}
import bookingtour.protocols.core.values.enumeration.{AppItem, LangItem}

/**
  * © Alexey Toroshchin 2020.
  */
final case class Attach(
    sessionId: SessionIdValue,
    app: AppItem,
    lang: LangItem,
    ip: RemoteValue,
    fingerprint: FingerprintValue,
    replayTo: ActorRef
)
