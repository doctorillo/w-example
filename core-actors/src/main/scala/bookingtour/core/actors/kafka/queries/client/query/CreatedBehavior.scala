package bookingtour.core.actors.kafka.queries.client.query

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent.SessionCreated
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait CreatedBehavior[Query, Value] {
  _: Actor with ActorLogging with Stash with State[Query, Value] with DeleteBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. created-channel."

  private final def behaviors(createChannel: EdgeProducerChannelCreated): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      deleteBehavior(createChannel, msg)

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def createdBehavior(createChannel: EdgeProducerChannelCreated): Unit = {
    context.become(behaviors(createChannel))
    val msg = KafkaEdge.>.makeConsumerChannel[SessionCreated](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionCreatedEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
