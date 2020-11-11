package bookingtour.protocols.api

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class UuidStringA(uuid: UUID, value: String)

object UuidStringA {
  import cats.Order
  import cats.instances.int._
  import cats.syntax.eq._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  trait ToJsonOps {
    implicit final val uuidStringAEnc: Encoder[UuidStringA] = deriveEncoder
    implicit final val uuidStringADec: Decoder[UuidStringA] = deriveDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val uuidStringAO: Order[UuidStringA] = (x: UuidStringA, y: UuidStringA) => {
      val uuid = x.uuid.compareTo(y.uuid)
      val name = x.value.compareTo(y.value)
      if (uuid =!= 0) {
        uuid
      } else {
        name
      }
    }
  }

  final object order extends ToOrderOps
}
