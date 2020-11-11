package bookingtour.protocols.core

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2020.
  */
package object sessions {
  @newtype final case class SolverIdValue(x: UUID)

  @newtype final case class SessionIdKey(x: String)

  @newtype final case class SessionIdValue(x: UUID)

  @newtype final case class RemoteValue(x: String)

  @newtype final case class FingerprintKey(x: String)

  @newtype final case class FingerprintValue(x: String)
}
