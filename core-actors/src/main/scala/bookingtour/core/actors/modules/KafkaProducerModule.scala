package bookingtour.core.actors.modules

import akka.Done
import akka.kafka.ProducerSettings
import bookingtour.core.actors.kafka.pool.producer.AlpakkaKafkaProducer
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.modules.BaseModule
import com.typesafe.config.Config
import org.apache.kafka.common.serialization.StringSerializer

/**
  * © Alexey Toroshchin 2019.
  */
final class KafkaProducerModule private (
    implicit val alpakkaKafkaProducer: MessageProducer[Done]
)

object KafkaProducerModule {
  final def apply()(
      implicit bm: BaseModule,
      am: AkkaModule
  ): KafkaProducerModule = {
    import am._
    import bm._
    val b = if (bm.isProduction) {
      bm.appConfig.getString("kafka.bootstrap")
    } else {
      bm.appConfig.getString("kafka.bootstrapDev")
    }
    val kg: Config  = appConfig.getConfig("akka.kafka")
    val pc: Config  = kg.getConfig("producer")
    val kt: Boolean = kg.getBoolean("enable-trace")
    val ps: ProducerSettings[String, String] =
      ProducerSettings(pc, new StringSerializer, new StringSerializer)
        .withBootstrapServers(b)
    implicit val kp: MessageProducer[Done] =
      AlpakkaKafkaProducer.make(settings = ps, enableTrace = kt)
    new KafkaProducerModule
  }
}
