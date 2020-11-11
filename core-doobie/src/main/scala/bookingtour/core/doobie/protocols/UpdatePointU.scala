package bookingtour.core.doobie.protocols

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdatePointU(
    id: UUID,
    dataId: UUID,
    solverId: UUID,
    lng: Double,
    lat: Double
)

object UpdatePointU {
  import cats.Order
  import cats.instances.int._
  import cats.syntax.order._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  trait ToJsonOps {
    implicit final val updatePointUEnc: Encoder[UpdatePointU] = deriveEncoder
    implicit final val updatePointUDec: Decoder[UpdatePointU] = deriveDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val updatePointUO: Order[UpdatePointU] =
      (x: UpdatePointU, y: UpdatePointU) => {
        val id       = x.id.compareTo(y.id)
        val dataId   = x.dataId.compareTo(y.dataId)
        val solverId = x.solverId.compareTo(y.solverId)
        val lng      = x.lng.compareTo(y.lng)
        val lat      = x.lat.compareTo(y.lat)
        Array(id, dataId, solverId, lng, lat).foldLeft(0) { (acc, x) =>
          if (acc =!= 0) {
            acc
          } else
            x
        }
      }
  }

  final object order extends ToOrderOps
}
