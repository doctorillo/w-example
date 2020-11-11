package bookingtour.protocols.core.actors.kafka

import bookingtour.protocols.core.types.CompareOps
import cats.Order

/**
  * © Alexey Toroshchin 2019.
  */
final case class StreamGroup(topic: String, groupId: String)

object StreamGroup {
  implicit final val streamGroupO: Order[StreamGroup] = (x: StreamGroup, y: StreamGroup) =>
    CompareOps.compareFn(x.topic.compareTo(y.topic), x.groupId.compareTo(y.groupId))
}
