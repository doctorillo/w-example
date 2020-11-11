package bookingtour.protocols.core.actors.kafka

import akka.Done
import akka.actor.ActorRef
import akka.kafka.scaladsl.Consumer.DrainingControl

/**
  * © Alexey Toroshchin 2019.
  */
final case class TopicConsumerWorker(
    groupId: String,
    topic: String,
    consumer: DrainingControl[Done],
    worker: ActorRef
)

object TopicConsumerWorker {
  import bookingtour.protocols.core.types.CompareOps
  import cats.Order

  implicit final val topicConsumerWorkerO: Order[TopicConsumerWorker] =
    (x: TopicConsumerWorker, y: TopicConsumerWorker) =>
      CompareOps.compareFn(
        x.topic.compareTo(y.topic),
        x.groupId.compareTo(y.groupId),
        x.worker.compareTo(y.worker)
      )
}
