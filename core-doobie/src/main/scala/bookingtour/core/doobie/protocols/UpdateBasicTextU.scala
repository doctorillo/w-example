package bookingtour.core.doobie.protocols

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateBasicTextU(
    id: UUID,
    solverId: UUID,
    text: String
)

object UpdateBasicTextU {
  import cats.Order
  import cats.instances.int._
  import cats.syntax.order._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  trait ToJsonOps {
    implicit final val updateBasicTextUEnc: Encoder[UpdateBasicTextU] = deriveEncoder
    implicit final val updateBasicTextUDec: Decoder[UpdateBasicTextU] = deriveDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val updateBasicTextUO: Order[UpdateBasicTextU] =
      (x: UpdateBasicTextU, y: UpdateBasicTextU) => {
        val id       = x.id.compareTo(y.id)
        val solverId = x.solverId.compareTo(y.solverId)
        val text     = x.text.compareTo(y.text)
        Array(id, solverId, text).foldLeft(0) { (acc, x) =>
          if (acc =!= 0) {
            acc
          } else
            x
        }
      }
  }

  final object order extends ToOrderOps
}
