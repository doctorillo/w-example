package bookingtour.protocols.core.actors.channels.distribution

import java.util.UUID

import bookingtour.protocols.core.register.{RegisterEntity, RegisterEntityCirce}
import io.circe.derivation._
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DistributionChannelCommand(val sessionId: UUID, val tag: Int) extends Product with Serializable

object DistributionChannelCommand {
  final case class DChannelCreate(
      override val sessionId: UUID,
      override val tag: Int = 0,
      targetTag: String,
      consumerTopic: String
  ) extends DistributionChannelCommand(sessionId, tag)

  final case class DChannelDelete(
      override val sessionId: UUID,
      override val tag: Int = 1
  ) extends DistributionChannelCommand(sessionId, tag)

  final case class DChannelSetHeartbeat(
      override val sessionId: UUID,
      override val tag: Int = 2,
      waitSec: Long
  ) extends DistributionChannelCommand(sessionId, tag)

  implicit final val distributionChannelCreateEnc: Encoder[DChannelCreate] =
    deriveEncoder
  implicit final val distributionChannelCreateDec: Decoder[DChannelCreate] =
    deriveDecoder
  implicit final val distributionChannelDeleteEnc: Encoder[DChannelDelete] =
    deriveEncoder
  implicit final val distributionChannelDeleteDec: Decoder[DChannelDelete] =
    deriveDecoder
  implicit final val distributionChannelSetHeartbeatEnc: Encoder[DChannelSetHeartbeat] =
    deriveEncoder
  implicit final val distributionChannelSetHeartbeatDec: Decoder[DChannelSetHeartbeat] =
    deriveDecoder

  implicit final val distributionChannelCommandEnc: Encoder[DistributionChannelCommand] = {
    case v: DChannelCreate =>
      v.asJson
    case v: DChannelDelete =>
      v.asJson
    case v: DChannelSetHeartbeat =>
      v.asJson
  }
  implicit final val distributionChannelCommandDec: Decoder[DistributionChannelCommand] =
    (c: HCursor) => {
      Decoder[Int].prepare(_.downField("tag"))(c) match {
        case Left(thr: DecodingFailure) =>
          Left(thr)

        case Right(0) =>
          distributionChannelCreateDec(c)

        case Right(1) =>
          distributionChannelDeleteDec(c)

        case Right(2) =>
          distributionChannelSetHeartbeatDec(c)

        case Right(x: Int) =>
          Left(DecodingFailure(s"receive $x", List.empty))

      }
    }

  implicit final val distributionChannelCreateRE: RegisterEntity.Aux[DChannelCreate] =
    RegisterEntityCirce[DChannelCreate]()

  implicit final val distributionChannelDeleteRE: RegisterEntity.Aux[DChannelDelete] =
    RegisterEntityCirce[DChannelDelete]()

  implicit final val distributionChannelSetHeartbeatRE: RegisterEntity.Aux[DChannelSetHeartbeat] =
    RegisterEntityCirce[DChannelSetHeartbeat]()
}
