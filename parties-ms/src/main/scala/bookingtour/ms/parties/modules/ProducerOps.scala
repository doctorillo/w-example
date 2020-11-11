package bookingtour.ms.parties.modules

import bookingtour.core.actors.kafka.state.producer.DStateProducer
import bookingtour.core.actors.modules.{AkkaModule, KafkaEdgeModule, KafkaTopicModule, StateEnvModule}
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.parties.agg.basic._
import bookingtour.protocols.parties.agg.basic.{
  CityAgg,
  CompanyAgg,
  CountryAgg,
  PickupPointAgg,
  RegionAgg,
  SolverDataAgg
}
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
final class ProducerOps private (
    implicit bm: BaseModule,
    rm: RuntimeModule,
    am: AkkaModule,
    sm: StateEnvModule,
    kt: KafkaTopicModule,
    em: KafkaEdgeModule,
    ago: AggregateOps
) {
  import ago._
  import am._
  import bm._
  import em._
  import kt._
  import rm._
  import sm._

  private val tagsConfig: Config   = appConfig.getConfig("tagged-channel.registry-parties")
  private val channelTopic: String = topics.partiesTopic

  DStateProducer.make[CountryAgg, CountryAgg.Id](
    uniqueTag = "country-producer",
    targetTag = tagsConfig.getString("country-agg"),
    inputTopic = channelTopic,
    dataLink = countryAgg,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )

  DStateProducer.make[RegionAgg, RegionAgg.Id](
    uniqueTag = "region-producer",
    targetTag = tagsConfig.getString("region-agg"),
    inputTopic = channelTopic,
    dataLink = regionAgg,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )

  DStateProducer.make[CityAgg, CityAgg.Id](
    uniqueTag = "city-producer",
    targetTag = tagsConfig.getString("city-agg"),
    inputTopic = channelTopic,
    dataLink = cityAGG,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )

  DStateProducer.make[PickupPointAgg, PickupPointAgg.Id](
    uniqueTag = "pickup-point-producer",
    targetTag = tagsConfig.getString("pickup-points"),
    inputTopic = channelTopic,
    dataLink = pickupPointAGG,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )

  DStateProducer.make[CompanyAgg, CompanyAgg.Id](
    uniqueTag = "company-producer",
    targetTag = tagsConfig.getString("company-agg"),
    inputTopic = channelTopic,
    dataLink = companyAGG,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )

  DStateProducer.make[ProviderDataAgg, ProviderDataAgg.Id](
    uniqueTag = "provider-data-producer",
    targetTag = tagsConfig.getString("provider-data"),
    inputTopic = channelTopic,
    dataLink = providerDataAGG,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )

  DStateProducer.make[SolverDataAgg, SolverDataAgg.Id](
    uniqueTag = "solver-data-producer",
    targetTag = tagsConfig.getString("solver-data"),
    inputTopic = channelTopic,
    dataLink = solverDataAGG,
    ttl = ttlSeconds,
    enableTrace = enableTrace
  )
}

object ProducerOps {
  final def apply()(
      implicit bm: BaseModule,
      rm: RuntimeModule,
      am: AkkaModule,
      sm: StateEnvModule,
      kt: KafkaTopicModule,
      em: KafkaEdgeModule,
      ago: AggregateOps
  ): ProducerOps = new ProducerOps
}
