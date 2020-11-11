package bookingtour.core.actors.kafka.pool.edge

import akka.Done
import akka.actor.{Actor, ActorLogging}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.core.actors.kafka.pool.hearbeat.consumer.HeartbeatConsumer
import bookingtour.core.actors.kafka.pool.hearbeat.consumer.HeartbeatConsumer.HeartbeatConsumerRef
import bookingtour.core.actors.kafka.pool.producer.EdgeProducer
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.actors.kafka.EdgeCommand.{EdgeConsumerCreateWrapper, EdgeProducerCreateWrapper}
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeTopicUnreachableReceived}
import bookingtour.protocols.core.actors.kafka.{EdgeHeartbeat, TopicConsumerWorker, TopicProducerWorker}
import bookingtour.protocols.core.register.RegisterEntity
import bookingtour.protocols.core.types.FunctionKCore.instances._
import cats.instances.string._
import cats.syntax.option._
import cats.syntax.order._
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
protected[edge] trait BasicBehavior {
  _: Actor with ActorLogging =>

  private final val Self: EdgeRef = self.coerce[EdgeRef]

  protected final def basicBehavior(
      uniqueTag: String,
      mainTopic: String,
      heartbeatTopic: String,
      consumers: Set[TopicConsumerWorker],
      producers: List[TopicProducerWorker],
      heartbeats: List[(String, HeartbeatConsumerRef)],
      heartbeatInterval: Long,
      enableTrace: Boolean
  )(
      implicit r: zio.Runtime[zio.ZEnv],
      p: MessageProducer[Done],
      e: RegisterEntity.Aux[EdgeHeartbeat]
  ): Receive = {
    case wrapper: EdgeConsumerCreateWrapper[_] =>
      val tag = s"$uniqueTag. edge-consumer-channel-create receive"
      consumers.find(_.topic === wrapper.msg.topic) match {
        case None =>
          val error = s"$tag. topic ${wrapper.msg.topic} not found."
          log.error(error)
          wrapper.mediatorRef ! EdgeChannelError(
            id = wrapper.msg.id,
            error = error,
            replayTo = self
          )

        case Some(worker) =>
          worker.worker.forward(wrapper)
      }

    case wrapper: EdgeProducerCreateWrapper[_] =>
      val tag = s"$uniqueTag. edge-producer-channel-create receive. topic: ${wrapper.msg.topic}"
      val heartbeatRef = heartbeats.find(_._1 === wrapper.msg.topic) match {
        case None =>
          val heartbeatRef: HeartbeatConsumerRef = HeartbeatConsumer.make(
            edgeRef = Self,
            topic = wrapper.msg.topic,
            heartbeatTopic = heartbeatTopic,
            heartbeatInterval = heartbeatInterval,
            enableTrace = enableTrace
          )
          if (enableTrace) {
            log.info(s"$tag. created.")
          }
          (wrapper.msg.topic, heartbeatRef).some

        case Some(_) =>
          none
      }
      producers.find(x => x.key === wrapper.msg.register.key && x.topic === wrapper.msg.topic) match {
        case None =>
          implicit val re: RegisterEntity.Aux[wrapper.msg.register.Result] = wrapper.msg.register
          val producer = EdgeProducer.make[wrapper.msg.register.Result](
            topic = wrapper.msg.topic,
            enableTrace = enableTrace
          )
          producer.x.forward(wrapper)
          context.become(
            basicBehavior(
              uniqueTag = uniqueTag,
              mainTopic = mainTopic,
              heartbeatTopic = heartbeatTopic,
              consumers = consumers,
              producers = producers :+ TopicProducerWorker(
                key = wrapper.msg.register.key,
                topic = wrapper.msg.topic,
                worker = producer.x
              ),
              heartbeats = heartbeats ++ heartbeatRef.liftFK[List],
              heartbeatInterval = heartbeatInterval,
              enableTrace = enableTrace
            )
          )

        case Some(producer) =>
          producer.worker.forward(wrapper)
      }

    case msg @ EdgeTopicUnreachableReceived(_, topic, _) =>
      log.error(s"$uniqueTag. edge-topic-unreachable-received. topic: $topic.")
      producers.filter(_.topic === topic).map(_.worker ! msg)

    case msg =>
      log.error(s"$uniqueTag. receive unhandled $msg from ${sender()}.")
      log.error(s"$uniqueTag. shutdown.")
      context.stop(self)
  }
}
