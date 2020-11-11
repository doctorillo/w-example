package bookingtour.protocols.core.actors.distributions

import java.util.UUID

import scala.reflect.runtime.universe._

import bookingtour.protocols.core.register.{RegisterEntity, RegisterEntityCirce}
import io.circe.derivation._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class DistributionQueryCommand(val sessionId: UUID) extends Product with Serializable

object DistributionQueryCommand {
  final case class SessionCreate(
      override val sessionId: UUID,
      targetTag: String,
      consumerTopic: String
  ) extends DistributionQueryCommand(sessionId)

  final case class SessionDelete(override val sessionId: UUID) extends DistributionQueryCommand(sessionId)

  final case class SessionFetch(override val sessionId: UUID) extends DistributionQueryCommand(sessionId)

  final case class SessionQuery[A](override val sessionId: UUID, query: A) extends DistributionQueryCommand(sessionId)

  implicit final val sessionCreateEnc: Encoder[SessionCreate] = deriveEncoder
  implicit final val sessionCreateDec: Decoder[SessionCreate] = deriveDecoder
  implicit final val sessionDeleteEnc: Encoder[SessionDelete] = deriveEncoder
  implicit final val sessionDeleteDec: Decoder[SessionDelete] = deriveDecoder
  implicit final val sessionFetchEnc: Encoder[SessionFetch]   = deriveEncoder
  implicit final val sessionFetchDec: Decoder[SessionFetch]   = deriveDecoder

  implicit final def sessionQueryEnc[A: Encoder]: Encoder[SessionQuery[A]] = deriveEncoder

  implicit final def sessionQueryDec[A: Decoder]: Decoder[SessionQuery[A]] = deriveDecoder

  implicit final val sessionCreateRE: RegisterEntity.Aux[SessionCreate] =
    RegisterEntityCirce[SessionCreate]()
  implicit final val sessionDeleteRE: RegisterEntity.Aux[SessionDelete] =
    RegisterEntityCirce[SessionDelete]()
  implicit final val sessionFetchRE: RegisterEntity.Aux[SessionFetch] =
    RegisterEntityCirce[SessionFetch]()

  implicit final def sessionQueryRE[A: Encoder: Decoder](
      implicit attag: WeakTypeTag[SessionQuery[A]]
  ): RegisterEntity.Aux[SessionQuery[A]] =
    RegisterEntityCirce[SessionQuery[A]]()
}
