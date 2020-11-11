package bookingtour.api.customers.config

import bookingtour.core.actors.modules._
import bookingtour.protocols.core.actors.kafka.StreamGroup
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import cats.data.NonEmptySet

/**
  * © Alexey Toroshchin 2019.
  */
final class ApiRuntime private (val httpHost: String, val httpPort: Int)(
    implicit val bm: BaseModule,
    val rm: RuntimeModule,
    val am: AkkaModule,
    val sem: StateEnvModule,
    val ktm: KafkaTopicModule,
    val kem: KafkaEdgeModule,
    val cm: CacheModule,
    val sm: SessionModule,
    val qcm: QueryOperationClientModule,
    val qom: QueryOrderClientModule
)

object ApiRuntime {
  final def apply(path: String): ApiRuntime = {
    val serviceTag: String      = "api-customer"
    implicit val bm: BaseModule = BaseModule(fileName = path, service = serviceTag)
    import bm._
    implicit val rm: RuntimeModule        = RuntimeModule.default(enableTrace)
    implicit val am: AkkaModule           = AkkaModule(serviceTag)
    implicit val sem: StateEnvModule      = StateEnvModule(serviceTag)
    implicit val ktm: KafkaTopicModule    = KafkaTopicModule()
    implicit val kpm: KafkaProducerModule = KafkaProducerModule()
    import ktm._
    implicit val kem: KafkaEdgeModule = KafkaEdgeModule(
      uniqueTag = s"$serviceTag-edge",
      mainTopic = topics.apiCustomerTopic,
      consumeTopics = NonEmptySet.of(
        StreamGroup(topic = topics.apiCustomerTopic, groupId = s"$nodeId-$serviceTag-kt-0"),
        StreamGroup(topic = topics.heartbeatTopic, groupId = s"$nodeId-$serviceTag-kt-1")
      )
    )
    implicit val cm: CacheModule                 = CacheModule()
    implicit val qcm: QueryOperationClientModule = QueryOperationClientModule()
    implicit val qom: QueryOrderClientModule     = QueryOrderClientModule()
    implicit val sm: SessionModule               = SessionModule()
    import bm._
    val httpPort: Int    = appConfig.getInt("ms.api-customer.port")
    val httpHost: String = appConfig.getString("ms.api-customer.host")
    new ApiRuntime(httpHost = httpHost, httpPort = httpPort)
  }
}
