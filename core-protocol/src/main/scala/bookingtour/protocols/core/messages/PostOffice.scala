package bookingtour.protocols.core.messages

import cats.Order
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class PostOffice(name: String)

object PostOffice {
  implicit final val postOfficeEnc: Encoder[PostOffice] = io.circe.derivation.deriveEncoder
  implicit final val postOfficeDec: Decoder[PostOffice] = io.circe.derivation.deriveDecoder
  implicit final val postOfficeO: Order[PostOffice]     = (x: PostOffice, y: PostOffice) => x.name.compareTo(y.name)
}
