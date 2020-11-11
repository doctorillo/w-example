package bookingtour.ms.parties.modules

import bookingtour.core.actors.kafka.state.consumer.DStateConsumer
import bookingtour.core.actors.modules.{AkkaModule, KafkaEdgeModule, KafkaTopicModule, StateEnvModule}
import bookingtour.core.actors.primitives.channel.basic.BasicChannelActor
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core._
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.interlook.source.geo.{CityEP, CountryEP, PickupPointEP, RegionEP}
import bookingtour.protocols.interlook.source.parties.{CustomerGroupEP, PartnerEP, PropertyStarEP, SupplierGroupEP}
import cats.instances.all._
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
final class ConsumerOps private (
    implicit bm: BaseModule,
    rm: RuntimeModule,
    am: AkkaModule,
    kt: KafkaTopicModule,
    ke: KafkaEdgeModule,
    sem: StateEnvModule
) {
  import am._
  import bm._
  import ke._
  import kt._
  import rm._
  import sem._

  private val tagsConfig: Config   = appConfig.getConfig("tagged-channel.inter-look")
  private val produceTopic: String = topics.interLookTopic

  val countryConsumer: ActorProducer[CountryEP, CountryEP.Id] =
    DStateConsumer.make[CountryEP, CountryEP.Id](
      uniqueTag = "country-consumer",
      targetTag = tagsConfig.getString("country"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[CountryEP, CountryEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val regionConsumer: ActorProducer[RegionEP, RegionEP.Id] =
    DStateConsumer.make[RegionEP, RegionEP.Id](
      uniqueTag = "region-consumer",
      targetTag = tagsConfig.getString("region"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[RegionEP, RegionEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val cityConsumer: ActorProducer[CityEP, CityEP.Id] =
    DStateConsumer.make[CityEP, CityEP.Id](
      uniqueTag = "city-consumer",
      targetTag = tagsConfig.getString("city"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[CityEP, CityEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val pickupPointConsumer: ActorProducer[PickupPointEP, PickupPointEP.Id] =
    DStateConsumer.make[PickupPointEP, PickupPointEP.Id](
      uniqueTag = "pickup-point-consumer",
      targetTag = tagsConfig.getString("pickup-points"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[PickupPointEP, PickupPointEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val branchConsumer: ActorProducer[PartnerEP, PartnerEP.Id] =
    DStateConsumer.make[PartnerEP, PartnerEP.Id](
      uniqueTag = "branch-consumer",
      targetTag = tagsConfig.getString("branch"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[PartnerEP, PartnerEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val partnerConsumer: ActorProducer[PartnerEP, PartnerEP.Id] =
    DStateConsumer.make[PartnerEP, PartnerEP.Id](
      uniqueTag = "partner-consumer",
      targetTag = tagsConfig.getString("partner"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[PartnerEP, PartnerEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val propertyConsumer: ActorProducer[PartnerEP, PartnerEP.Id] =
    DStateConsumer.make[PartnerEP, PartnerEP.Id](
      uniqueTag = "property-consumer",
      targetTag = tagsConfig.getString("property"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[PartnerEP, PartnerEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val propertyStarConsumer: ActorProducer[PropertyStarEP, PropertyStarEP.Id] =
    DStateConsumer.make[PropertyStarEP, PropertyStarEP.Id](
      uniqueTag = "property-star-consumer",
      targetTag = tagsConfig.getString("property-star"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor.makeChannel[PropertyStarEP, PropertyStarEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val customerGroupConsumer: ActorProducer[CustomerGroupEP, CustomerGroupEP.Id] =
    DStateConsumer.make[CustomerGroupEP, CustomerGroupEP.Id](
      uniqueTag = "customer-group-consumer",
      targetTag = tagsConfig.getString("customer-group"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor
        .makeChannel[CustomerGroupEP, CustomerGroupEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )

  val supplierGroupConsumer: ActorProducer[SupplierGroupEP, SupplierGroupEP.Id] =
    DStateConsumer.make[SupplierGroupEP, SupplierGroupEP.Id](
      uniqueTag = "supplier-group-consumer",
      targetTag = tagsConfig.getString("supplier-group"),
      outputTopic = produceTopic,
      inputTopic = mainTopic,
      channelFactory = BasicChannelActor
        .makeChannel[SupplierGroupEP, SupplierGroupEP.Id, Int](trace = enableTrace),
      ttl = ttlSeconds,
      connectTimeoutSeconds = heartBeatWaitSeconds,
      enableTrace = enableTrace
    )
}

object ConsumerOps {
  final def apply()(
      implicit bm: BaseModule,
      rm: RuntimeModule,
      am: AkkaModule,
      kt: KafkaTopicModule,
      ke: KafkaEdgeModule,
      sem: StateEnvModule
  ): ConsumerOps = new ConsumerOps
}
