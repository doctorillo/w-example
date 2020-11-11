package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent.SessionAnswerReceived
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait AnswerBehavior[Query, Value] {
  _: Actor with ActorLogging with Stash with State[Query, Value] with EmptyAnswerBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. answer-channel."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      emptyAnswerBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        answerChannel = msg
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def answerBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated
  ): Unit = {
    log.info(s"$tag start.")
    context.become(
      behaviors(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel
      )
    )
    val msg = KafkaEdge.>.makeConsumerChannel[SessionAnswerReceived[Value]](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionAnswerEntity,
      _ => true,
      dropBefore = Instant.now(),
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
