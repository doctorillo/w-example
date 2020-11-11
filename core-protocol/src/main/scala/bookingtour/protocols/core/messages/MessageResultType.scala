package bookingtour.protocols.core.messages

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class MessageResultType(val value: Int, val name: String) extends IntEnumEntry

case object MessageResultType extends IntEnum[MessageResultType] with IntCirceEnum[MessageResultType] {
  override def values: immutable.IndexedSeq[MessageResultType] = findValues

  final case object Normal extends MessageResultType(value = 0, name = "normal")

  final case object Error extends MessageResultType(value = 1, name = "error")

  final case object AccessDenied extends MessageResultType(value = 2, name = "access denied")

  trait ToJsonOps {
    implicit final val messageResultTypeEnc: Encoder[MessageResultType] = circeEncoder
    implicit final val messageResultTypeDec: Decoder[MessageResultType] = circeDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val messageResultTypeO: Order[MessageResultType] =
      (x: MessageResultType, y: MessageResultType) => x.value.compare(y.value)
  }

  final object order extends ToOrderOps
}
