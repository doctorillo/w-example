package bookingtour.protocols.api.contexts.queries

import bookingtour.protocols.core._
import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue}
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AttachEnvQ(
    sessionId: SessionIdValue,
    fingerprint: FingerprintValue,
    ip: RemoteValue,
    lang: LangItem
)
