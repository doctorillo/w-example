package bookingtour.protocols.core.actors.channels.query

import java.util.UUID

import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.util.Timeout
import bookingtour.protocols.core.actors.channels.query.ChannelFetchEvent.{AnswerReceived, EmptyReceived, ErrorReceived}
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelFetchCommand(val channelId: UUID, val replayTo: ActorRef) extends Product with Serializable

object ChannelFetchCommand {
  final case class FetchStatus(override val channelId: UUID, override val replayTo: ActorRef)
      extends ChannelFetchCommand(channelId, replayTo)

  final case class Fetch(override val channelId: UUID, override val replayTo: ActorRef)
      extends ChannelFetchCommand(channelId, replayTo)

  final case class FetchIfNotRunning(override val channelId: UUID, override val replayTo: ActorRef)
      extends ChannelFetchCommand(channelId, replayTo)

  final case class FetchIfReady(override val channelId: UUID, override val replayTo: ActorRef)
      extends ChannelFetchCommand(channelId, replayTo)

  final case class FetchWithKeyFilter[Key](
      override val channelId: UUID,
      condition: Key => Boolean,
      override val replayTo: ActorRef
  ) extends ChannelFetchCommand(channelId, replayTo)

  final case class FetchWithValueFilter[Value](
      override val channelId: UUID,
      condition: Value => Boolean,
      override val replayTo: ActorRef
  ) extends ChannelFetchCommand(channelId, replayTo)

  final case class FetchWithKeyValueFilter[Key, Value](
      override val channelId: UUID,
      conditionKey: Key => Boolean,
      conditionValue: Value => Boolean,
      override val replayTo: ActorRef
  ) extends ChannelFetchCommand(channelId, replayTo)

  final case class FetchWithMap[A, B](
      override val channelId: UUID,
      map: A => List[B],
      override val replayTo: ActorRef
  ) extends ChannelFetchCommand(channelId, replayTo)

  final object actors {
    import akka.pattern.extended._

    def askReadyNonEmptyData[A](
        producer: ActorRef,
        id: UUID,
        tag: String
    )(implicit timeout: Timeout = 3.seconds): ZIO[Any, String, List[A]] = {
      for {
        a <- ZIO.fromFuture { implicit ec =>
              producer
                .ask(ref => FetchIfReady(id, ref))
                .mapTo[ChannelFetchEvent]
            }.catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
        b <- a match {
              case AnswerReceived(_, data) =>
                ZIO
                  .effect(data.asInstanceOf[List[A]])
                  .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))

              case ErrorReceived(_, cause) =>
                ZIO.fail(cause.headOption.map(_.getMessage).getOrElse(s"$tag. error-received"))

              case _ =>
                ZIO.fail(s"$tag. state empty.")
            }
      } yield b
    }

    def askData[A](
        producer: ActorRef,
        id: UUID,
        tag: String
    )(implicit timeout: Timeout = 3.seconds): ZIO[Any, String, List[A]] = {
      for {
        a <- ZIO
              .fromFuture(implicit ec =>
                producer
                  .ask(ref => Fetch(id, ref))
                  .mapTo[ChannelFetchEvent]
              )
              .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
        b <- a match {
              case EmptyReceived(_) =>
                ZIO.effectTotal(List.empty[A])

              case AnswerReceived(_, data) =>
                ZIO
                  .effect(data.asInstanceOf[List[A]])
                  .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))

              case ErrorReceived(_, cause) =>
                ZIO.fail(cause.headOption.map(_.getMessage).getOrElse(s"$tag. error-received"))

              case msg =>
                ZIO.fail(s"$tag. unhandled ${msg.getClass.getName}.")
            }
      } yield b
    }

    def askDataIfNotRunning[A](
        producer: ActorRef,
        id: UUID,
        tag: String
    )(implicit timeout: Timeout = 3.seconds): ZIO[Any, String, List[A]] = {
      for {
        a <- ZIO
              .fromFuture(implicit ec =>
                producer
                  .ask(ref => FetchIfNotRunning(id, ref))
                  .mapTo[ChannelFetchEvent]
              )
              .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
        b <- a match {
              case EmptyReceived(_) =>
                ZIO.effectTotal(List.empty[A])

              case AnswerReceived(_, data) =>
                ZIO
                  .effect(data.asInstanceOf[List[A]])
                  .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))

              case ErrorReceived(_, cause) =>
                ZIO.fail(cause.headOption.map(_.getMessage).getOrElse(s"$tag. error-received"))

              case msg =>
                ZIO.fail(s"$tag. unhandled ${msg.getClass.getName}.")
            }
      } yield b
    }
  }
}
