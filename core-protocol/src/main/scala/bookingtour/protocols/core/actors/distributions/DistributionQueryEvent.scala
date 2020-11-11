package bookingtour.protocols.core.actors.distributions

import java.util.UUID

import scala.reflect.runtime.universe._

import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.register.{RegisterEntity, RegisterEntityCirce}
import bookingtour.protocols.core.types.CompareOps
import cats.Order
import io.circe.derivation._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DistributionQueryEvent(val sessionId: UUID) extends Product with Serializable

object DistributionQueryEvent {
  final case class SessionCreated(
      override val sessionId: UUID,
      targetTag: String,
      consumerTopic: String
  ) extends DistributionQueryEvent(sessionId)

  final object SessionCreated {
    implicit val sessionCreatedEnc: Encoder[SessionCreated] = deriveEncoder
    implicit val sessionCreatedDec: Decoder[SessionCreated] = deriveDecoder
    implicit val sessionCreatedO: Order[SessionCreated] =
      (x: SessionCreated, y: SessionCreated) =>
        CompareOps.compareFn(
          x.sessionId.compareTo(y.sessionId),
          x.consumerTopic.compareTo(y.consumerTopic),
          x.targetTag.compareTo(y.targetTag)
        )
    implicit val sessionCreatedRE: RegisterEntity.Aux[SessionCreated] =
      RegisterEntityCirce[SessionCreated]()
  }

  final case class SessionDeleted(override val sessionId: UUID) extends DistributionQueryEvent(sessionId)

  final object SessionDeleted {
    implicit val sessionDeletedEnc: Encoder[SessionDeleted] = deriveEncoder
    implicit val sessionDeletedDec: Decoder[SessionDeleted] = deriveDecoder
    implicit val sessionDeletedRE: RegisterEntity.Aux[SessionDeleted] =
      RegisterEntityCirce[SessionDeleted]()
  }

  final case class SessionEmptyReceived(override val sessionId: UUID) extends DistributionQueryEvent(sessionId)

  final object SessionEmptyReceived {
    implicit val sessionEmptyReceivedEnc: Encoder[SessionEmptyReceived] = deriveEncoder
    implicit val sessionEmptyReceivedDec: Decoder[SessionEmptyReceived] = deriveDecoder
    implicit val sessionEmptyReceivedRE: RegisterEntity.Aux[SessionEmptyReceived] =
      RegisterEntityCirce[SessionEmptyReceived]()
  }

  final case class SessionAnswerReceived[A](override val sessionId: UUID, data: List[A])
      extends DistributionQueryEvent(sessionId)

  final object SessionAnswerReceived {
    implicit def sessionAnswerReceivedEnc[A: Encoder]: Encoder[SessionAnswerReceived[A]] =
      deriveEncoder

    implicit def sessionAnswerReceivedDec[A: Decoder]: Decoder[SessionAnswerReceived[A]] =
      deriveDecoder

    implicit def sessionAnswerReceivedRE[A: Encoder: Decoder](
        implicit attag: WeakTypeTag[SessionAnswerReceived[A]]
    ): RegisterEntity.Aux[SessionAnswerReceived[A]] =
      RegisterEntityCirce[SessionAnswerReceived[A]]()
  }

  final case class SessionStatusChangedReceived(override val sessionId: UUID, status: ChannelStatus)
      extends DistributionQueryEvent(sessionId)

  final object SessionStatusChangedReceived {
    implicit val sessionStatusChangedReceivedEnc: Encoder[SessionStatusChangedReceived] =
      deriveEncoder
    implicit val sessionStatusChangedReceivedDec: Decoder[SessionStatusChangedReceived] =
      deriveDecoder
    implicit val sessionStatusChangedReceivedRE: RegisterEntity.Aux[
      SessionStatusChangedReceived
    ] =
      RegisterEntityCirce[SessionStatusChangedReceived]()
  }
}
