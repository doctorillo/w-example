package bookingtour.core.actors.kafka.queries.client.query

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.>
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent.SessionDeleted
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait DeletedBehavior[Query, Value] {
  _: Actor with Stash with ActorLogging with State[Query, Value] with QueryBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. deleted-channel."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      queryBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = msg
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def deletedBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel
      )
    )
    val msg = >.makeConsumerChannel[SessionDeleted](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionDeletedEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
