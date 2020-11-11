package bookingtour.core.actors.kafka.queries.backend

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.SessionQuery
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait QueryBehavior[R, Query, Answer] {
  _: Actor with Stash with ActorLogging with State[R, Query, Answer] with StatusChangedBehavior[R, Query, Answer] =>

  private final val tag: String =
    s"$uniqueTag. topic: $targetTag. query-behavior."

  private final def behaviors(
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated,
      deletedChannel: EdgeProducerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      statusChangedBehavior(
        signalChannel = signalChannel,
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
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated,
      deletedChannel: EdgeProducerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        signalChannel = signalChannel,
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel
      )
    )
    val msg = KafkaEdge.>.makeConsumerChannel[SessionQuery[Query]](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionQueryEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    consumerPoolRef.x ! msg(self)
  }
}
