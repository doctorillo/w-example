package bookingtour.core.actors.kafka.queries.backend

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.SessionDelete
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait DeleteBehavior[R, Query, Answer] {
  _: Actor with ActorLogging with Stash with State[R, Query, Answer] with DeletedBehavior[R, Query, Answer] =>

  private final val tag: String =
    s"$uniqueTag. topic: $targetTag. delete-behavior."

  private final def behaviors(
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag edge-consumer-channel-created.")
      }
      deletedBehavior(
        signalChannel = signalChannel,
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
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        signalChannel = signalChannel,
        createChannel = createChannel,
        createdChannel = createdChannel
      )
    )
    val msg = KafkaEdge.>.makeConsumerChannel[SessionDelete](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionDeleteEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    consumerPoolRef.x ! msg(self)
  }
}
