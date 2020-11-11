package bookingtour.core.actors.kafka.state.producer

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelCommand.DChannelCreate
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeConsumerChannelCreated}

/**
  * © Alexey Toroshchin 2019.
  */
protected[producer] trait ChannelCreateBehavior[Value, Id] {
  _: Actor with ActorLogging with Stash with State[Value, Id] with ChannelDeleteBehavior[Value, Id] =>

  private final val tag: String =
    s"$uniqueTag. tag: $targetTag. channel-create-behavior"

  private final def behaviors(stateChannel: ChannelCreated): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag. created. key: ${createEntity.key.typeTag}.")
      }
      channelDeleteBehavior(stateChannel, msg)

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err.")
      shutdown()

    case _ =>
      stash()
  }

  protected def channelCreateBehavior(
      stateChannel: ChannelCreated
  ): Unit = {
    context.become(behaviors(stateChannel))
    val msg = KafkaEdge.>.makeConsumerChannel[DChannelCreate](
      uniqueTag = uniqueTag,
      topic = inputTopic,
      register = createEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
