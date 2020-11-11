package bookingtour.protocols.parties.api.sessions

import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue, SolverIdValue}

/**
  * © Alexey Toroshchin 2019.
  */
final case class SessionContractAPI(
    id: SessionIdValue,
    fingerprint: FingerprintValue,
    ip: RemoteValue,
    solver: Option[SolverIdValue]
)
