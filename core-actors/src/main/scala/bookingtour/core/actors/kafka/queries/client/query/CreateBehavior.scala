package bookingtour.core.actors.kafka.queries.client.query

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeProducerChannelCreated}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait CreateBehavior[Query, Value] {
  _: Actor with ActorLogging with Stash with State[Query, Value] with CreatedBehavior[Query, Value] =>

  private final val tag: String = s"$uniqueTag. create-channel."

  private final def behaviors(): Receive = {
    case msg: EdgeProducerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag consumer-channel-created.")
      }
      createdBehavior(msg)

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def createBehavior(): Unit = {
    context.become(behaviors())
    val msg = KafkaEdge.>.makeProducerChannel(
      uniqueTag = uniqueTag,
      topic = route.output,
      register = sessionCreateEntity,
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
