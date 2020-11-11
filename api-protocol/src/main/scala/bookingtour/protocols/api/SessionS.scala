package bookingtour.protocols.api

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class SessionS(
    sessionId: UUID,
    solverId: UUID,
    partyId: UUID,
    langEnv: String = "ru",
    fingerprint: String,
    ip: String
)

object SessionS {
  import io.circe.derivation._
  import io.circe.{Decoder, Encoder}

  final object json {
    implicit val sessionSEnc: Encoder[SessionS] = deriveEncoder
    implicit val sessionSDec: Decoder[SessionS] = deriveDecoder
  }
}
