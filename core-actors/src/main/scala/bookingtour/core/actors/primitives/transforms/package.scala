package bookingtour.core.actors.primitives

import java.util.UUID

import akka.actor.ActorRef
import akka.event.LoggingAdapter
import bookingtour.protocols.actors.aggregators.AggregateResult
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.{
  ChannelPushCreate,
  ChannelPushDelete,
  ChannelPushUpdate
}
import zio.Exit
import zio.Exit.{Failure, Success}

/**
  * © Alexey Toroshchin 2019.
  */
package object transforms {
  final case object CheckDrain

  sealed trait TransformExit
  final case object TransformCompleted extends TransformExit
  final case object TransformFailed    extends TransformExit

  final def cbArrow(
      self: ActorRef,
      tag: String,
      state: ActorRef,
      stateId: UUID,
      onError: () => Unit,
      log: LoggingAdapter
  ): Exit[String, AggregateResult[_]] => Unit = {
    case Failure(cause) =>
      val errors = cause.failures
      errors.foreach(err => log.error(s"$tag. $err."))
      onError()
      self ! TransformFailed

    case Success(AggregateResult(created, updated, deleted, actual)) =>
      log.info(
        s"$tag. aggregate-result received. created: ${created.length}. updated: ${updated.length}. deleted: ${deleted.length}. state: ${actual.length}."
      )
      if (deleted.nonEmpty) {
        state ! ChannelPushDelete(
          stateId,
          deleted
        )
      }
      if (created.nonEmpty) {
        state ! ChannelPushCreate(
          stateId,
          created
        )
      }
      if (updated.nonEmpty) {
        state ! ChannelPushUpdate(
          stateId,
          updated
        )
      }
      self ! TransformCompleted
  }
}
