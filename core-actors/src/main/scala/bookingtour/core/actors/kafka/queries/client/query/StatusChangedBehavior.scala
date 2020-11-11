package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.SessionCreate
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent.SessionStatusChangedReceived
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.EdgePublish

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait StatusChangedBehavior[Query, Value] {
  _: Actor with ActorLogging with Stash with State[Query, Value] with BasicBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. status-changed-channel."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeConsumerChannelCreated,
      emptyAnswerChannel: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      unstashAll()
      basicBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        answerChannel = answerChannel,
        emptyAnswerChannel = emptyAnswerChannel,
        statusChangedChannel = msg,
        queries = List.empty
      )
      createChannel.replayTo ! EdgePublish(
        id = createChannel.id,
        channel = taggedChannel,
        msg = SessionCreate(
          sessionId = sessionId,
          targetTag = targetTag,
          consumerTopic = route.input
        ),
        expiredAt = Instant.now().plusSeconds(60)
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def statusChangedBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeConsumerChannelCreated,
      emptyAnswerChannel: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        answerChannel = answerChannel,
        emptyAnswerChannel = emptyAnswerChannel
      )
    )
    val msg = KafkaEdge.>.makeConsumerChannel[SessionStatusChangedReceived](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionStatusChangedEntity,
      _ => true,
      dropBefore = Instant.now(),
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
