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
protected[query] trait DeleteBehavior[Query, Value] {
  _: Actor with Stash with ActorLogging with State[Query, Value] with DeletedBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. delete-channel."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeProducerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      deletedBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = msg
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def deleteBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(behaviors(createChannel = createChannel, createdChannel = createdChannel))
    val msg = KafkaEdge.>.makeProducerChannel(
      uniqueTag = uniqueTag,
      topic = route.output,
      register = sessionDeleteEntity,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
