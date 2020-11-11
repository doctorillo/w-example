package bookingtour.core.actors.kafka.state

import java.util.UUID

import scala.concurrent.duration._

import akka.pattern.extended.ask
import akka.util.Timeout
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.query.ChannelFetchCommand.{
  Fetch,
  FetchWithKeyFilter,
  FetchWithKeyValueFilter,
  FetchWithValueFilter
}
import bookingtour.protocols.core.actors.channels.query.ChannelFetchEvent
import bookingtour.protocols.core.actors.channels.query.ChannelFetchEvent.{
  AnswerReceived,
  EmptyReceived,
  ErrorReceived,
  StatusReceived
}
import cats.Order
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveConsumerAlg[PartitionKey, Value] private (
    consumer: ActorProducer[Value, _],
    timeoutMs: Long,
    ordering: Ordering[Value]
) extends ConsumerAlg {
  implicit private val timeout: Timeout = timeoutMs.milliseconds
  val consumerAlg: ConsumerAlg.Aux[Any, PartitionKey, Value] = new ConsumerAlg.Service[Any] {
    final type Key    = PartitionKey
    final type Result = Value

    private def onResponse(response: ChannelFetchEvent): ZIO[Any, String, List[Value]] =
      response match {
        case StatusReceived(_, _) =>
          ZIO.fail("error: status received.")

        case ErrorReceived(_, error) =>
          ZIO.fail(s"error: $error")

        case EmptyReceived(_) =>
          ZIO.succeed(List.empty[Value])

        case AnswerReceived(_, data) =>
          ZIO
            .effect(data.asInstanceOf[List[Value]].sorted(ordering))
            .catchAll(thr => ZIO.fail(thr.getMessage))
      }

    def all(): ZIO[Any, String, List[Value]] =
      ZIO
        .fromFuture(implicit ec =>
          ask(
            actorRef = consumer.x,
            messageFactory = replayTo =>
              Fetch(
                channelId = UUID.randomUUID(),
                replayTo = replayTo
              )
          )(timeout).mapTo[ChannelFetchEvent]
        )
        .catchAll(thr => ZIO.fail(s"error: ${thr.getMessage}"))
        .flatMap(onResponse)

    def byKey(condition: Key => Boolean): ZIO[Any, String, List[Value]] =
      ZIO
        .fromFuture(implicit ec =>
          ask(
            actorRef = consumer.x,
            messageFactory = replayTo =>
              FetchWithKeyFilter(
                channelId = UUID.randomUUID(),
                condition = condition,
                replayTo = replayTo
              )
          )(timeout).mapTo[ChannelFetchEvent]
        )
        .catchAll(thr => ZIO.fail(s"error: ${thr.getMessage}"))
        .flatMap(onResponse)

    def byValue(condition: Value => Boolean): ZIO[Any, String, List[Value]] =
      ZIO
        .fromFuture(implicit ec =>
          ask(
            actorRef = consumer.x,
            messageFactory = replayTo =>
              FetchWithValueFilter(
                channelId = UUID.randomUUID(),
                condition = condition,
                replayTo = replayTo
              )
          )(timeout).mapTo[ChannelFetchEvent]
        )
        .catchAll(thr => ZIO.fail(s"error: ${thr.getMessage}"))
        .flatMap(onResponse)

    def byKeyValue(
        conditionKey: PartitionKey => Boolean,
        conditionValue: Value => Boolean
    ): ZIO[Any, String, List[Value]] =
      ZIO
        .fromFuture(implicit ec =>
          ask(
            actorRef = consumer.x,
            messageFactory = replayTo =>
              FetchWithKeyValueFilter(
                channelId = UUID.randomUUID(),
                conditionKey = conditionKey,
                conditionValue = conditionValue,
                replayTo = replayTo
              )
          )(timeout).mapTo[ChannelFetchEvent]
        )
        .catchAll(thr => ZIO.fail(s"error: ${thr.getMessage}"))
        .flatMap(onResponse)
  }
}

object LiveConsumerAlg {
  final def apply[PartitionKey, Value](
      consumer: ActorProducer[Value, _],
      timeoutMs: Long
  )(implicit o: Order[Value]): LiveConsumerAlg[PartitionKey, Value] =
    new LiveConsumerAlg[PartitionKey, Value](consumer, timeoutMs, o.toOrdering)
}
