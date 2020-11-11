package bookingtour.protocols.parties.api

import cats.data.NonEmptyChain

/**
  * © Alexey Toroshchin 2019.
  */
final case class SolverConfig(email: String, password: String, users: NonEmptyChain[UserConfig])
