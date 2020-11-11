package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent.SessionEmptyReceived
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait EmptyAnswerBehavior[Query, Value] {
  _: Actor with Stash with ActorLogging with State[Query, Value] with StatusChangedBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. empty-answer-channel."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      statusChangedBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        answerChannel = answerChannel,
        emptyAnswerChannel = msg
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def emptyAnswerBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        answerChannel = answerChannel
      )
    )
    val msg = KafkaEdge.>.makeConsumerChannel[SessionEmptyReceived](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionEmptyAnswerEntity,
      _ => true,
      dropBefore = Instant.now().minusSeconds(60L),
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
