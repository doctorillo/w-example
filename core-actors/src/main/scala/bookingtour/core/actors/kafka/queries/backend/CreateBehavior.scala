package bookingtour.core.actors.kafka.queries.backend

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.SessionCreate
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeConsumerChannelCreated}

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait CreateBehavior[R, Query, Answer] {
  _: Actor with ActorLogging with Stash with State[R, Query, Answer] with CreatedBehavior[R, Query, Answer] =>

  private final val tag: String =
    s"$uniqueTag. topic: $targetTag. create-behavior."

  private final def behaviors(signalChannel: SignalChannelCreated): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag edge-consumer-channel-created.")
      }
      createdBehavior(signalChannel = signalChannel, createChannel = msg)

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag $err")
      shutdown()

    case _ =>
      stash()
  }

  protected def createBehavior(signalChannel: SignalChannelCreated): Unit = {
    context.become(behaviors(signalChannel))
    val msg = KafkaEdge.>.makeConsumerChannel[SessionCreate](
      uniqueTag = uniqueTag,
      topic = route.input,
      register = sessionCreateEntity,
      filter = _ => true,
      dropBefore = dropBefore,
      replayTo = self
    )
    consumerPoolRef.x ! msg(self)
  }
}
