package bookingtour.protocols.core.actors.kafka

import akka.actor.ActorRef
import bookingtour.protocols.core.register.RegisterKey

/**
  * © Alexey Toroshchin 2019.
  */
final case class TopicProducerWorker(
    key: RegisterKey,
    topic: String,
    worker: ActorRef
)

object TopicProducerWorker {
  import bookingtour.protocols.core.types.CompareOps
  import cats.Order
  import cats.syntax.order._

  implicit final val topicProducerWorkerO: Order[TopicProducerWorker] =
    (x: TopicProducerWorker, y: TopicProducerWorker) =>
      CompareOps.compareFn(
        x.key.compare(y.key),
        x.topic.compareTo(y.topic),
        x.worker.compareTo(y.worker)
      )
}
