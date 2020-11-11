package bookingtour.protocols.core.messages

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.instances.string._
import cats.instances.uuid._
import cats.syntax.order._
import io.circe.derivation._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class TaggedChannel(val tag: String) extends Product with Serializable {
  def sameChannel(input: TaggedChannel): Boolean
}
object TaggedChannel {
  final case class ChannelTag(override val tag: String) extends TaggedChannel(tag) {
    def sameChannel(input: TaggedChannel): Boolean = input match {
      case x: ChannelTag =>
        this === x

      case x: ChannelSession =>
        this.tag === x.tag
    }
  }
  final object ChannelTag {
    implicit val circeEnc: Encoder.AsObject[ChannelTag] = deriveEncoder
    implicit val circeDec: Decoder[ChannelTag]          = deriveDecoder
    implicit val catsO: Order[ChannelTag]               = (x: ChannelTag, y: ChannelTag) => x.tag.compareTo(y.tag)
  }

  final case class ChannelSession(sessionId: UUID, override val tag: String) extends TaggedChannel(tag) {
    def sameChannel(input: TaggedChannel): Boolean = input match {
      case x: ChannelTag =>
        this.tag === x.tag

      case x: ChannelSession =>
        this === x
    }
  }
  final object ChannelSession {
    implicit val circeEnc: Encoder.AsObject[ChannelSession] = deriveEncoder
    implicit val circeDec: Decoder[ChannelSession]          = deriveDecoder
    implicit val catsO: Order[ChannelSession] = (x: ChannelSession, y: ChannelSession) =>
      CompareOps.compareFn(
        x.sessionId.compare(y.sessionId),
        x.tag.compareTo(y.tag)
      )
  }
  implicit final val taggedChannelEnc: Encoder.AsObject[TaggedChannel] = deriveEncoder
  implicit final val taggedChannelDec: Decoder[TaggedChannel]          = deriveDecoder
  implicit final val taggedChannelO: Order[TaggedChannel] =
    (x: TaggedChannel, y: TaggedChannel) =>
      (x, y) match {
        case (xx: ChannelTag, yy: ChannelTag) =>
          xx.compare(yy)

        case (xx: ChannelSession, yy: ChannelSession) =>
          xx.compare(yy)

        case (_: ChannelTag, _: ChannelSession) =>
          -1

        case (_: ChannelSession, _: ChannelTag) =>
          1
      }
}
