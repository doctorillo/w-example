package bookingtour.core.doobie

import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
trait BaseConfig {
  self =>
  val appConfig: Config
  val isProduction: Boolean

  final object topics {
    def heartbeatTopic: String = appConfig.getString("ms.heartbeat.kafka.in")

    def interLookWatchTopic: String = appConfig.getString("kafka.watch.interlook.queue")

    def interLookPropertyPricesWatchTopic: String =
      appConfig.getString("kafka.watch.interlook-property-prices.queue")

    def partiesWatchTopic: String = appConfig.getString("kafka.watch.parties.queue")

    def propertiesWatchTopic: String = appConfig.getString("kafka.watch.properties.queue")

    def ordersWatchTopic: String = appConfig.getString("kafka.watch.orders.queue")

    def partiesTopic: String = appConfig.getString("ms.parties.kafka.in")

    def propertiesTopic: String = appConfig.getString("ms.properties.kafka.in")

    def interLookTopic: String = appConfig.getString("ms.interlook.kafka.in")

    def apiCustomerTopic: String = appConfig.getString("api.customer.kafka.in")

    def operationCustomerTopic: String = appConfig.getString("ms.operation-customer.kafka.in")
  }
  final lazy val kafkaBootstrap: String = if (isProduction) {
    appConfig.getString("kafka.bootstrap")
  } else {
    appConfig.getString("kafka.bootstrapDev")
  }
  final lazy val kafkaConfig: Config    = appConfig.getConfig("akka.kafka")
  final lazy val consumerConfig: Config = kafkaConfig.getConfig("consumer")
  final lazy val producerConfig: Config = kafkaConfig.getConfig("producer")
  final lazy val heartBeatSeconds: Long = appConfig.getLong("akka.kafka.heart-beat-seconds")
  final lazy val heartBeatWaitWindowSeconds: Long =
    Math.round(heartBeatSeconds + 7)
  final lazy val kafkaTrace: Boolean = kafkaConfig.getBoolean("enable-trace")
}
