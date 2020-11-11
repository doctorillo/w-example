package bookingtour.protocols.api.users

import bookingtour.protocols.core.types.CompareOps.compareFn
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class SignInQ(name: String, password: String)

object SignInQ {
  implicit final val signInQEnc: Encoder[SignInQ] = deriveEncoder
  implicit final val signInQDec: Decoder[SignInQ] = deriveDecoder

  implicit final val signInO: Order[SignInQ] = (x: SignInQ, y: SignInQ) =>
    compareFn(
      x.name.compareTo(y.name),
      x.password.compareTo(y.password)
    )
}
