package bookingtour.protocols.core.register

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class RegisterKey(
    typeTag: String,
    constant: Boolean,
    version: Int = 1
)

object RegisterKey {
  implicit final val bodyRegisterKeyEnc: Encoder[RegisterKey] =
    io.circe.derivation.deriveEncoder
  implicit final val bodyRegisterKeyDec: Decoder[RegisterKey] =
    io.circe.derivation.deriveDecoder

  implicit final val bodyRegisterKeyO: Order[RegisterKey] =
    (x: RegisterKey, y: RegisterKey) =>
      CompareOps.compareFn(
        x.typeTag.compareTo(y.typeTag),
        x.constant.compareTo(y.constant),
        x.version.compareTo(y.version)
      )

  final def instance[T](
      typeName: String,
      constant: Boolean = false,
      version: Int = 1
  ): RegisterKey = new RegisterKey(typeName, constant, version)
}
