package bookingtour.core.actors.kafka

import akka.kafka.ProducerSettings
import org.apache.kafka.clients.producer.Producer

/**
  * © Alexey Toroshchin 2019.
  */
package object pool {
  final val HEADER_ENVELOPE: String = "envelope"
  final def makeProducer(
      producerSettings: ProducerSettings[String, String]
  ): Producer[String, String] = producerSettings.createKafkaProducer()
}
