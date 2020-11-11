package bookingtour.core.kafka

import bookingtour.protocols.core.messages.MessageEnvelope
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
trait MessageProducer[T] {
  def publishMessage(
      envelope: MessageEnvelope,
      message: String
  ): ZIO[Any, String, T]

  def publishEffect(
      envelope: MessageEnvelope,
      message: ZIO[Any, String, String]
  ): ZIO[Any, String, T]
}
