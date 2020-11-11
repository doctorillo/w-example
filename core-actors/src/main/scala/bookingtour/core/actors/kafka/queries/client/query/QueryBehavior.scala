package bookingtour.core.actors.kafka.queries.client.query

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait QueryBehavior[Query, Value] {
  _: Actor with Stash with ActorLogging with State[Query, Value] with AnswerBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. query-channel."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeProducerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      answerBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = msg
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def queryBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel
      )
    )
    val msg = KafkaEdge.>.makeProducerChannel(
      uniqueTag = uniqueTag,
      topic = route.output,
      register = sessionQueryEntity,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
