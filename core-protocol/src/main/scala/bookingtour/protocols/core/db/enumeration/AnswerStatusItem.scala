package bookingtour.protocols.core.db.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class AnswerStatusItem(val value: Int) extends IntEnumEntry

case object AnswerStatusItem extends IntEnum[AnswerStatusItem] with IntCirceEnum[AnswerStatusItem] {
  override def values: immutable.IndexedSeq[AnswerStatusItem] = findValues

  case object Empty    extends AnswerStatusItem(value = 0)
  case object NonEmpty extends AnswerStatusItem(value = 1)

  trait ToJsonOps {
    implicit final val answerStatusItemEnc: Encoder[AnswerStatusItem] = circeEncoder
    implicit final val answerStatusItemDec: Decoder[AnswerStatusItem] = circeDecoder
  }

  final object json extends ToJsonOps

  trait ToOrderOps {
    implicit final val answerStatusItemO: Order[AnswerStatusItem] =
      (x: AnswerStatusItem, y: AnswerStatusItem) => x.value.compare(y.value)
  }

  final object order extends ToOrderOps
}
