package bookingtour.protocols.api

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class SessionCookieS(sessionId: UUID, solverId: UUID, fingerprint: String, ip: String)
