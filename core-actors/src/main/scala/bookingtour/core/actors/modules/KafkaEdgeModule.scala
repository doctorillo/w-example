package bookingtour.core.actors.modules

import akka.kafka.ConsumerSettings
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.protocols.core.actors.kafka.StreamGroup
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import cats.data.NonEmptySet
import org.apache.kafka.common.serialization.StringDeserializer

/**
  * © Alexey Toroshchin 2019.
  */
final class KafkaEdgeModule private (val mainTopic: String, edge: EdgeRef) {
  implicit val wrapper: KafkaEdge.KafkaEdgeWrapper = KafkaEdge.KafkaEdgeWrapper(edge)
}

object KafkaEdgeModule {
  final def apply(uniqueTag: String, mainTopic: String, consumeTopics: NonEmptySet[StreamGroup])(
      implicit bm: BaseModule,
      am: AkkaModule,
      rm: RuntimeModule,
      kpm: KafkaProducerModule
  ): KafkaEdgeModule = {
    import am._
    import bm._
    import kpm._
    import rm._
    val b = if (bm.isProduction) {
      bm.appConfig.getString("kafka.bootstrap")
    } else {
      bm.appConfig.getString("kafka.bootstrapDev")
    }
    val kc = appConfig.getConfig("akka.kafka")
    val cs =
      ConsumerSettings(kc.getConfig("consumer"), new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(b)
    val ht = appConfig.getString("ms.heartbeat.kafka.in")
    val edge: EdgeRef =
      KafkaEdge.make(
        uniqueTag = s"$uniqueTag",
        consumerSettings = cs,
        mainTopic = mainTopic,
        heartbeatTopic = ht,
        topics = consumeTopics.toSortedSet,
        heartbeatInterval = appConfig.getLong("akka.kafka.heart-beat-seconds"),
        enableTrace = kc.getBoolean("enable-trace")
      )
    new KafkaEdgeModule(mainTopic, edge)
  }
}
