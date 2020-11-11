package bookingtour.protocols.core.actors.operations

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class OpCommand extends Product with Serializable

object OpCommand {
  final case object Start extends OpCommand

  final case object Completed   extends OpCommand
  final case object Stop        extends OpCommand
  final case object Timeout     extends OpCommand
  final case object RunTruncate extends OpCommand
  final case object ReConnect   extends OpCommand

  final case object ReConnectCanceled                                   extends OpCommand
  final case class SendHeartbeatSession(sessionId: UUID, topic: String) extends OpCommand
}
