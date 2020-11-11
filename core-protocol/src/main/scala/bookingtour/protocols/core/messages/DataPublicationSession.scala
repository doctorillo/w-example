package bookingtour.protocols.core.messages

import java.util.UUID

/**
  * © Alexey Toroshchin 2020.
  */
sealed trait DataPublicationSession {
  val sessionId: UUID
  val tag: String
  val producer: String
  val subscriber: String
}

object DataPublicationSession {}
