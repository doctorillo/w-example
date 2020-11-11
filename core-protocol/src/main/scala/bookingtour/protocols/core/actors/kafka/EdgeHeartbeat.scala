package bookingtour.protocols.core.actors.kafka

import cats.instances.long._
import cats.instances.string._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class EdgeHeartbeat(topic: String, interval: Long)
