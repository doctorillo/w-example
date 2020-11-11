package bookingtour.protocols.core.actors.channels.signal

import java.util.UUID

import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.pattern.extended._
import akka.util.Timeout
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalMutationEvent.SignalChannelMutationReceived
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelSignalMutationCommand(val channelId: UUID) extends Product with Serializable

object ChannelSignalMutationCommand {
  final case class SignalChannelDequeueMutations(override val channelId: UUID, replayTo: ActorRef)
      extends ChannelSignalMutationCommand(channelId)

  final object actors {
    def askReadyProducer[A](
        channel: ActorRef,
        id: UUID,
        tag: String
    )(implicit timeout: Timeout = 3.seconds): ZIO[Any, String, ActorRef] = {
      for {
        a <- ZIO.fromFuture { implicit ec =>
              channel
                .ask(replayTo => SignalChannelDequeueMutations(id, replayTo))
                .mapTo[SignalChannelMutationReceived]
            }.catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
        b <- a match {
              case SignalChannelMutationReceived(_, ChannelStatus.Ready, producer) =>
                ZIO.effectTotal(producer)
              case SignalChannelMutationReceived(_, s, _) =>
                ZIO.fail(s"$tag. status $s not valid.")
            }
      } yield b
    }
    def askProducer[A](
        channel: ActorRef,
        id: UUID,
        tag: String
    )(implicit timeout: Timeout = 3.seconds): ZIO[Any, String, ActorRef] =
      ZIO.fromFuture { implicit ec =>
        channel
          .ask(replayTo => SignalChannelDequeueMutations(id, replayTo))
          .mapTo[SignalChannelMutationReceived]
      }.map(_.producer).catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
  }
}
