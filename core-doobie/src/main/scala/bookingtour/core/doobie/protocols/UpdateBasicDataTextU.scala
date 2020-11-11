package bookingtour.core.doobie.protocols

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateBasicDataTextU(
    id: UUID,
    dataId: UUID,
    solverId: UUID,
    text: String
)

object UpdateBasicDataTextU {
  import cats.Order
  import cats.instances.int._
  import cats.syntax.order._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  trait ToJsonOps {
    implicit final val updateBasicDataTextUEnc: Encoder[UpdateBasicDataTextU] = deriveEncoder
    implicit final val updateBasicDataTextUDec: Decoder[UpdateBasicDataTextU] = deriveDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val updateBasicDataTextUO: Order[UpdateBasicDataTextU] =
      (x: UpdateBasicDataTextU, y: UpdateBasicDataTextU) => {
        val id       = x.id.compareTo(y.id)
        val dataId   = x.dataId.compareTo(y.dataId)
        val solverId = x.solverId.compareTo(y.solverId)
        val text     = x.text.compareTo(y.text)
        Array(id, dataId, solverId, text).foldLeft(0) { (acc, x) =>
          if (acc =!= 0) {
            acc
          } else
            x
        }
      }
  }

  final object order extends ToOrderOps
}
