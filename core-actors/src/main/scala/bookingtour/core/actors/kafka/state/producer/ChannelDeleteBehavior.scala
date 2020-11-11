package bookingtour.core.actors.kafka.state.producer

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelCommand.DChannelDelete
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeConsumerChannelCreated}

/**
  * © Alexey Toroshchin 2019.
  */
protected trait ChannelDeleteBehavior[Value, Id] {
  _: Actor with ActorLogging with Stash with State[Value, Id] with BasicBehavior[Value, Id] =>

  private final val tag: String =
    s"$uniqueTag. tag: $targetTag. delete-behavior"

  private final def behaviors(
      stateChannel: ChannelCreated,
      createChannel: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag. created. key: ${deleteEntity.key.typeTag}.")
      }
      unstashAll()
      basicBehavior(makeState(stateChannel, createChannel, msg, log))

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag. $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def channelDeleteBehavior(
      stateChannel: ChannelCreated,
      createChannel: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(
      behaviors(
        stateChannel = stateChannel,
        createChannel = createChannel
      )
    )
    val msg = KafkaEdge.>.makeConsumerChannel[DChannelDelete](
      uniqueTag = uniqueTag,
      topic = inputTopic,
      register = deleteEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
