package bookingtour.protocols.core.actors.channels.distribution

import java.util.UUID

import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import io.circe.derivation._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DistributionChannelEvent(
    val sessionId: UUID,
    val sequenceId: SequenceNr,
    val announceSequenceId: SequenceNr
) extends Product with Serializable

object DistributionChannelEvent {
  final case class DChannelCreated(
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr,
      targetTag: String,
      consumerTopic: String
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelDeleted(
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelEmptySnapshotReceived(
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelSnapshotReceived[A](
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr,
      data: List[A]
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelItemCreated[A](
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr,
      data: List[A]
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelItemUpdated[A](
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr,
      data: List[A]
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelItemDeleted[A](
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr,
      data: List[A]
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  final case class DChannelStatusChanged(
      override val sessionId: UUID,
      override val sequenceId: SequenceNr,
      override val announceSequenceId: SequenceNr,
      status: ChannelStatus
  ) extends DistributionChannelEvent(
        sessionId = sessionId,
        sequenceId = sequenceId,
        announceSequenceId = announceSequenceId
      )

  implicit final val distributionChannelCreatedEnc: Encoder[DChannelCreated] =
    deriveEncoder
  implicit final val distributionChannelCreatedDec: Decoder[DChannelCreated] =
    deriveDecoder

  implicit final val distributionChannelDeletedEnc: Encoder[DChannelDeleted] =
    deriveEncoder
  implicit final val distributionChannelDeletedDec: Decoder[DChannelDeleted] =
    deriveDecoder

  implicit final val distributionChannelEmptySnapshotReceivedEnc: Encoder[
    DChannelEmptySnapshotReceived
  ] =
    deriveEncoder
  implicit final val distributionChannelEmptySnapshotReceivedDec: Decoder[
    DChannelEmptySnapshotReceived
  ] =
    deriveDecoder

  implicit final def distributionChannelSnapshotReceivedEnc[A: Encoder]: Encoder[
    DChannelSnapshotReceived[A]
  ] = deriveEncoder
  implicit final def distributionChannelSnapshotReceivedDec[A: Decoder]: Decoder[
    DChannelSnapshotReceived[A]
  ] = deriveDecoder

  implicit final def distributionChannelItemCreatedEnc[A: Encoder]: Encoder[
    DChannelItemCreated[A]
  ] = deriveEncoder
  implicit final def distributionChannelItemCreatedDec[A: Decoder]: Decoder[
    DChannelItemCreated[A]
  ] = deriveDecoder

  implicit final def distributionChannelItemUpdatedEnc[A: Encoder]: Encoder[
    DChannelItemUpdated[A]
  ] = deriveEncoder
  implicit final def distributionChannelItemUpdatedDec[A: Decoder]: Decoder[
    DChannelItemUpdated[A]
  ] = deriveDecoder

  implicit final def distributionChannelItemDeletedEnc[A: Encoder]: Encoder[
    DChannelItemDeleted[A]
  ] = deriveEncoder
  implicit final def distributionChannelItemDeletedDec[A: Decoder]: Decoder[
    DChannelItemDeleted[A]
  ] = deriveDecoder

  implicit final val distributionChannelStatusChangedEnc: Encoder[
    DChannelStatusChanged
  ] = deriveEncoder
  implicit final val distributionChannelStatusChangedDec: Decoder[
    DChannelStatusChanged
  ] = deriveDecoder
}
