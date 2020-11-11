package bookingtour.protocols.core.messages

import java.util.UUID

import bookingtour.protocols.core.register.RegisterKey

/**
  * © Alexey Toroshchin 2020.
  */
sealed trait DataPublication[A] {
  val sessionId: UUID
  val bodyKey: RegisterKey
}

object DataPublication {
  final case class BasicPublication[A](sessionId: UUID, bodyKey: RegisterKey, data: List[A]) extends DataPublication[A]
}
