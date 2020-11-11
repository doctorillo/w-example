package bookingtour.protocols.core.messages

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class MessageBodyType(val value: Int, val name: String) extends IntEnumEntry

case object MessageBodyType extends IntEnum[MessageBodyType] with IntCirceEnum[MessageBodyType] {
  override def values: immutable.IndexedSeq[MessageBodyType] = findValues

  final case object Snapshot extends MessageBodyType(value = 0, name = "snapshot")

  final case object Created extends MessageBodyType(value = 1, name = "created")

  final case object Updated extends MessageBodyType(value = 2, name = "updated")

  final case object Deleted extends MessageBodyType(value = 3, name = "deleted")

  final case object InterChange extends MessageBodyType(value = 4, name = "inter-change")

  trait ToJsonOps {
    implicit final val messageBodyTypeEnc: Encoder[MessageBodyType] = circeEncoder
    implicit final val messageBodyTypeDec: Decoder[MessageBodyType] = circeDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val messageBodyTypeO: Order[MessageBodyType] =
      (x: MessageBodyType, y: MessageBodyType) => x.value.compare(y.value)
  }

  final object order extends ToOrderOps
}
