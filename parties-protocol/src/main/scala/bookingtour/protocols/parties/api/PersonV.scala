package bookingtour.protocols.parties.api

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class PersonV(uuid: UUID, partyUuid: UUID, firstName: String, lastName: String)

object PersonV {
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  trait ToJsonOps {
    implicit final val personVEnc: Encoder[PersonV] = deriveEncoder
    implicit final val personVDec: Decoder[PersonV] = deriveDecoder
  }

  final object json extends ToJsonOps
}
