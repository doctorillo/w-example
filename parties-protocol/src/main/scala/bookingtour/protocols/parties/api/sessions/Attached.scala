package bookingtour.protocols.parties.api.sessions

import bookingtour.protocols.core.sessions.SessionIdValue

/**
  * © Alexey Toroshchin 2020.
  */
final case class Attached(sessionId: SessionIdValue, session: EnvApi)
