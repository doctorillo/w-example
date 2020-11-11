package bookingtour.core.actors.modules

import bookingtour.protocols.core.modules.BaseModule
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
final class KafkaTopicModule private (val topics: KafkaTopicModule.Topics)

object KafkaTopicModule {
  final class Topics(appConfig: Config) {
    val heartbeatTopic: String = appConfig.getString("ms.heartbeat.kafka.in")

    val interLookWatchTopic: String = appConfig.getString("kafka.watch.interlook.queue")

    val interLookWatchEnabled: Boolean = appConfig.getBoolean("kafka.watch.interlook.enable")

    val interLookPropertyPricesWatchTopic: String =
      appConfig.getString("kafka.watch.interlook-property-prices.queue")

    val interLookPropertyPricesWatchEnabled: Boolean =
      appConfig.getBoolean("kafka.watch.interlook-property-prices.enable")

    val partiesWatchTopic: String = appConfig.getString("kafka.watch.parties.queue")

    val partiesWatchEnabled: Boolean = appConfig.getBoolean("kafka.watch.parties.enable")

    val propertiesWatchTopic: String = appConfig.getString("kafka.watch.properties.queue")

    val propertiesWatchEnabled: Boolean = appConfig.getBoolean("kafka.watch.properties.enable")

    val ordersWatchTopic: String = appConfig.getString("kafka.watch.orders.queue")

    val ordersWatchEnabled: Boolean = appConfig.getBoolean("kafka.watch.orders.enable")

    val excursionsWatchTopic: String = appConfig.getString("kafka.watch.excursions.queue")

    val excursionsWatchEnabled: Boolean = appConfig.getBoolean("kafka.watch.excursions.enable")

    val partiesTopic: String = appConfig.getString("ms.parties.kafka.in")

    val propertiesTopic: String = appConfig.getString("ms.properties.kafka.in")

    val propertyPricesTopic: String = appConfig.getString("ms.inter-look-prices.kafka.in")

    val ordersTopic: String = appConfig.getString("ms.orders.kafka.in")

    val excursionsTopic: String = appConfig.getString("ms.excursions.kafka.in")

    val interLookTopic: String = appConfig.getString("ms.interlook.kafka.in")

    val apiCustomerTopic: String = appConfig.getString("ms.api-customer.kafka.in")

    val operationCustomerTopic: String = appConfig.getString("ms.operation-customer.kafka.in")
  }

  final def apply()(implicit bm: BaseModule): KafkaTopicModule = {
    import bm._
    val t = new Topics(appConfig)
    new KafkaTopicModule(t)
  }
}
