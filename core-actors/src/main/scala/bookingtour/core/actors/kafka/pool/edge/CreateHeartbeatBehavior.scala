package bookingtour.core.actors.kafka.pool.edge

import akka.Done
import akka.actor.{Actor, ActorLogging, Stash}
import akka.kafka.ConsumerSettings
import akka.stream.Materializer
import bookingtour.core.actors.kafka.pool.hearbeat.producer.HeartbeatProducer
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.actors.kafka.{EdgeHeartbeat, StreamGroup}
import bookingtour.protocols.core.register.RegisterEntity

/**
  * © Alexey Toroshchin 2019.
  */
protected[edge] trait CreateHeartbeatBehavior {
  _: Actor with Stash with ActorLogging with CreateConsumerBehavior =>

  protected final def createHeartbeatBehavior(
      uniqueTag: String,
      mainTopic: String,
      heartbeatTopic: String,
      consumerSettings: ConsumerSettings[String, String],
      topics: Set[StreamGroup],
      heartbeatInterval: Long,
      enableTrace: Boolean
  )(
      implicit mat: Materializer,
      r: zio.Runtime[zio.ZEnv],
      p: MessageProducer[Done],
      e: RegisterEntity.Aux[EdgeHeartbeat]
  ): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. create-heartbeat-behavior. $heartbeatTopic. enter.")
    }
    HeartbeatProducer.make(
      topic = mainTopic,
      heartbeatTopic = heartbeatTopic,
      interval = heartbeatInterval,
      enableTrace = enableTrace
    )
    createConsumerBehavior(
      uniqueTag = uniqueTag,
      mainTopic = mainTopic,
      heartbeatTopic = heartbeatTopic,
      consumerSettings = consumerSettings,
      topics = topics,
      heartbeatInterval = heartbeatInterval,
      enableTrace = enableTrace
    )
  }
}
