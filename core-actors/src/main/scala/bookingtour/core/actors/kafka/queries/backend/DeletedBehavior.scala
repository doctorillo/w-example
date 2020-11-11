package bookingtour.core.actors.kafka.queries.backend

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait DeletedBehavior[R, Query, Answer] {
  _: Actor with ActorLogging with Stash with State[R, Query, Answer] with QueryBehavior[R, Query, Answer] =>

  private final val tag: String =
    s"$uniqueTag. topic: $targetTag. deleted-behavior."

  private final def behaviors(
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeProducerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag edge-producer-channel-created.")
      }
      queryBehavior(
        signalChannel = signalChannel,
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
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        signalChannel = signalChannel,
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel
      )
    )
    val msg = KafkaEdge.>.makeProducerChannel(
      uniqueTag = uniqueTag,
      topic = route.output,
      register = sessionDeletedEntity,
      replayTo = self
    )
    consumerPoolRef.x ! msg(self)
  }
}
