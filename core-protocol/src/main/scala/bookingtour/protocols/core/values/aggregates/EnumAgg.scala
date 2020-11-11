package bookingtour.protocols.core.values.aggregates

import java.util.UUID

import bookingtour.protocols.core.values.db.LabelE
import cats.data.Chain
import enumeratum.values.IntEnumEntry

/**
  * © Alexey Toroshchin 2019.
  */
final case class EnumAgg[A <: IntEnumEntry](id: UUID, value: A, labels: Chain[LabelE])

object EnumAgg {
  type Id = UUID

  import bookingtour.protocols.core.types.CompareOps
  import cats.Order
  import cats.syntax.order._
  import io.circe.derivation._
  import io.circe.{Decoder, Encoder}

  implicit final def enumAggEnc[A <: IntEnumEntry: Encoder]: Encoder[EnumAgg[A]] = deriveEncoder
  implicit final def enumAggDec[A <: IntEnumEntry: Decoder]: Decoder[EnumAgg[A]] = deriveDecoder

  implicit final def enumAggO[A <: IntEnumEntry: Order]: Order[EnumAgg[A]] =
    (x: EnumAgg[A], y: EnumAgg[A]) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.value.compare(y.value),
        x.labels.compare(y.labels)
      )

  implicit final def enumAggR[A <: IntEnumEntry]: EnumAgg[A] => Id = x => x.id

  implicit final def enumAggPart[A <: IntEnumEntry]: EnumAgg[A] => Int = _ => 0
}
