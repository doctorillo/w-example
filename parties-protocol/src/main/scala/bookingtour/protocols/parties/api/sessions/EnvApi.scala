package bookingtour.protocols.parties.api.sessions

import java.time.Instant

import bookingtour.protocols.core._
import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue}
import bookingtour.protocols.core.values.enumeration.{AppItem, LangItem}
import bookingtour.protocols.parties.api.ui.ContextEnvUI
import bookingtour.protocols.parties.api.ui.ContextEnvUI
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
sealed abstract class EnvApi(
    val sessionId: SessionIdValue,
    val app: AppItem,
    val uiLang: LangItem,
    val ip: RemoteValue,
    val fingerprint: FingerprintValue,
    val created: Instant
) extends Product with Serializable

object EnvApi {
  @derive(encoder, decoder, order)
  final case class Guest(
      override val sessionId: SessionIdValue,
      override val app: AppItem,
      override val uiLang: LangItem,
      override val ip: RemoteValue,
      override val fingerprint: FingerprintValue,
      override val created: Instant
  ) extends EnvApi(sessionId, app, uiLang, ip, fingerprint, created)

  @derive(encoder, decoder, order)
  final case class Authorized(
      override val sessionId: SessionIdValue,
      override val app: AppItem,
      override val uiLang: LangItem,
      override val ip: RemoteValue,
      override val fingerprint: FingerprintValue,
      context: ContextEnvUI,
      override val created: Instant
  ) extends EnvApi(sessionId, app, uiLang, ip, fingerprint, created)
}
