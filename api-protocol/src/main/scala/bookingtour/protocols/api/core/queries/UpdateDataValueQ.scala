package bookingtour.protocols.api.core.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.instances.option._
import cats.instances.uuid._
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdateDataValueQ(id: Option[UUID], dataId: UUID, valueId: UUID, enable: Boolean)

object UpdateDataValueQ {
  implicit final val updateDataValueQEnc: Encoder[UpdateDataValueQ] = deriveEncoder
  implicit final val updateDataValueQDec: Decoder[UpdateDataValueQ] = deriveDecoder

  implicit final val updateDataValueQO: Order[UpdateDataValueQ] =
    (x: UpdateDataValueQ, y: UpdateDataValueQ) =>
      CompareOps.compareFn(
        x.id.compare(y.id),
        x.dataId.compareTo(y.dataId),
        x.valueId.compareTo(y.valueId),
        x.enable.compareTo(y.enable)
      )
}
