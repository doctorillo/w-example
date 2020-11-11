package bookingtour.core.doobie.protocols

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateDataValueU(
    id: Option[UUID],
    dataId: UUID,
    valueId: UUID,
    solverId: UUID,
    enable: Boolean
)

object UpdateDataValueU {
  import cats.Order
  import cats.instances.int._
  import cats.instances.option._
  import cats.instances.uuid._
  import cats.syntax.order._
  import io.circe.derivation.{deriveDecoder, deriveEncoder}
  import io.circe.{Decoder, Encoder}

  trait ToJsonOps {
    implicit final val updateDataValueUEnc: Encoder[UpdateDataValueU] = deriveEncoder
    implicit final val updateDataValueUDec: Decoder[UpdateDataValueU] = deriveDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val updateDataValueUO: Order[UpdateDataValueU] =
      (x: UpdateDataValueU, y: UpdateDataValueU) => {
        val id       = x.id.compare(y.id)
        val dataId   = x.dataId.compareTo(y.dataId)
        val valueId  = x.valueId.compareTo(y.valueId)
        val solverId = x.solverId.compareTo(y.solverId)
        val enable   = x.enable.compareTo(y.enable)
        Array(id, dataId, valueId, solverId, enable).foldLeft(0) { (acc, x) =>
          if (acc =!= 0) {
            acc
          } else
            x
        }
      }
  }

  final object order extends ToOrderOps
}
