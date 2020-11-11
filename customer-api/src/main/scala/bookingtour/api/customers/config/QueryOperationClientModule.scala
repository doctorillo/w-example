package bookingtour.api.customers.config

import java.time.Instant
import java.util.UUID

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.actors.kafka.queries.client.query.QueryCachedClient.QueryCacheConfig
import bookingtour.core.actors.modules.{AkkaModule, KafkaEdgeModule, KafkaTopicModule, StateEnvModule}
import bookingtour.core.finch.environment.impl.LiveCacheRedis
import bookingtour.protocols.api.booking.{FetchPriceVariantsQ, FetchPropertyCardQ, FetchPropertyDescriptionsQ}
import bookingtour.protocols.api.contexts.queries.FetchBusinessRelationQ
import bookingtour.protocols.api.core.queries.FetchEnumQ
import bookingtour.protocols.api.users.SignInQ
import bookingtour.protocols.core.cache.EntityKeyPrefix
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.excursions.FetchExcursionCardQ
import bookingtour.protocols.excursions.api.ExcursionCardUI
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.cmd.FetchPlannerSessionQ
import bookingtour.protocols.parties.api.queries.FetchPointsQ
import bookingtour.protocols.parties.api.ui.ContextEnvUI
import bookingtour.protocols.parties.api.{PartyValue, PointUI}
import bookingtour.protocols.properties.api.PropertyDescriptionUI
import bookingtour.protocols.property.prices.api.{PriceUnitUI, PropertyCardUI}
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
final class QueryOperationClientModule private (
    implicit bm: BaseModule,
    rm: RuntimeModule,
    am: AkkaModule,
    sem: StateEnvModule,
    ktm: KafkaTopicModule,
    kem: KafkaEdgeModule,
    cm: CacheModule
) {
  import am._
  import bm._
  import cm._
  import kem._
  import ktm._
  import rm._
  import sem._

  private val tagsConfig: Config = appConfig.getConfig("tagged-channel.operation-customer")
  implicit private val route: Bridge =
    Bridge(output = topics.operationCustomerTopic, input = topics.apiCustomerTopic)
  private val timeoutMillis = 3000L
  private val dropBefore    = Instant.now().minusSeconds(60L)

  private val signInCache: Option[QueryCacheConfig[SignInQ, ContextEnvUI]] =
    cacheProvider.map(x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[SignInQ, ContextEnvUI](
          prefix = EntityKeyPrefix("sign-in"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val signInModule: QueryModule[SignInQ, ContextEnvUI] =
    QueryModule.make[SignInQ, ContextEnvUI](
      uniqueTag = "sign-in",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("sign-in"),
      cacheConfig = signInCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  val plannerSessionModule: QueryModule[FetchPlannerSessionQ, PlannerSession] =
    QueryModule.make[FetchPlannerSessionQ, PlannerSession](
      uniqueTag = "planner-session",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("planner-session"),
      cacheConfig = None,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  private val businessRelationCache: Option[
    QueryCacheConfig[FetchBusinessRelationQ, PartyValue]
  ] =
    cacheProvider.map(implicit x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[FetchBusinessRelationQ, PartyValue](
          EntityKeyPrefix("business-relation"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val businessRelationModule: QueryModule[FetchBusinessRelationQ, PartyValue] =
    QueryModule.make[FetchBusinessRelationQ, PartyValue](
      uniqueTag = "business-relation-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("business-relation"),
      cacheConfig = businessRelationCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  private val pointsPropertyCache: Option[QueryCacheConfig[FetchPointsQ.Property, PointUI]] =
    cacheProvider.map(implicit x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[FetchPointsQ.Property, PointUI](
          EntityKeyPrefix("geo-point-client"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val pointsPropertyModule: QueryModule[FetchPointsQ.Property, PointUI] =
    QueryModule.make[FetchPointsQ.Property, PointUI](
      uniqueTag = "geo-point-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("geo-point"),
      cacheConfig = pointsPropertyCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  private val propertyCardsCache: Option[
    QueryCacheConfig[FetchPropertyCardQ, PropertyCardUI]
  ] = cacheProvider.map(implicit x =>
    QueryCacheConfig(
      alg = LiveCacheRedis.make[FetchPropertyCardQ, PropertyCardUI](
        EntityKeyPrefix("property-card-client"),
        cacheProvider = x
      ),
      cleanOnLoad = persistClean
    )
  )
  val propertyCardsModule: QueryModule[
    FetchPropertyCardQ,
    PropertyCardUI
  ] =
    QueryModule.make[FetchPropertyCardQ, PropertyCardUI](
      uniqueTag = "property-card-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("fetch-property-card-ui"),
      cacheConfig = propertyCardsCache,
      dropBefore = dropBefore,
      timeout = 10000L,
      enableTrace = enableTrace
    )

  private val excursionTagsCache: Option[
    QueryCacheConfig[FetchEnumQ, EnumAPI]
  ] = cacheProvider.map(implicit x =>
    QueryCacheConfig(
      alg = LiveCacheRedis.make[FetchEnumQ, EnumAPI](
        EntityKeyPrefix("excursion-tag-client"),
        cacheProvider = x
      ),
      cleanOnLoad = persistClean
    )
  )
  val excursionTagsModule: QueryModule[FetchEnumQ, EnumAPI] =
    QueryModule.make[FetchEnumQ, EnumAPI](
      uniqueTag = "excursion-tag-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("excursion-tag"),
      cacheConfig = excursionTagsCache,
      dropBefore = dropBefore,
      timeout = 10000L,
      enableTrace = enableTrace
    )

  private val excursionCardsCache: Option[
    QueryCacheConfig[FetchExcursionCardQ, ExcursionCardUI]
  ] = cacheProvider.map(implicit x =>
    QueryCacheConfig(
      alg = LiveCacheRedis.make[FetchExcursionCardQ, ExcursionCardUI](
        EntityKeyPrefix("excursion-card-client"),
        cacheProvider = x
      ),
      cleanOnLoad = persistClean
    )
  )
  val excursionCardsModule: QueryModule[FetchExcursionCardQ, ExcursionCardUI] =
    QueryModule.make[FetchExcursionCardQ, ExcursionCardUI](
      uniqueTag = "excursion-card-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("excursion-card"),
      cacheConfig = excursionCardsCache,
      dropBefore = dropBefore,
      timeout = 10000L,
      enableTrace = enableTrace
    )

  private val propertyDescriptionsCache: Option[
    QueryCacheConfig[FetchPropertyDescriptionsQ, PropertyDescriptionUI]
  ] = cacheProvider.map(implicit x =>
    QueryCacheConfig(
      alg = LiveCacheRedis.make[FetchPropertyDescriptionsQ, PropertyDescriptionUI](
        EntityKeyPrefix("property-description-client"),
        cacheProvider = x
      ),
      cleanOnLoad = persistClean
    )
  )

  val propertyDescriptionsModule: QueryModule[
    FetchPropertyDescriptionsQ,
    PropertyDescriptionUI
  ] =
    QueryModule.make[FetchPropertyDescriptionsQ, PropertyDescriptionUI](
      uniqueTag = "property-description-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("property-description"),
      cacheConfig = propertyDescriptionsCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  private val amenityCache: Option[QueryCacheConfig[FetchEnumQ, EnumAPI]] =
    cacheProvider.map(implicit x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[FetchEnumQ, EnumAPI](
          EntityKeyPrefix("amenity-enum-client"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val amenityModule: QueryModule[FetchEnumQ, EnumAPI] = QueryModule.make[FetchEnumQ, EnumAPI](
    uniqueTag = "amenity-enum-client",
    sessionId = UUID.randomUUID(),
    targetTag = tagsConfig.getString("amenity-enum"),
    cacheConfig = amenityCache,
    dropBefore = dropBefore,
    timeout = timeoutMillis,
    enableTrace = enableTrace
  )

  private val facilityCache: Option[QueryCacheConfig[FetchEnumQ, EnumAPI]] =
    cacheProvider.map(implicit x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[FetchEnumQ, EnumAPI](
          EntityKeyPrefix("facility-enum-client"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val facilityModule: QueryModule[FetchEnumQ, EnumAPI] = QueryModule.make[FetchEnumQ, EnumAPI](
    uniqueTag = "facility-enum-client",
    sessionId = UUID.randomUUID(),
    targetTag = tagsConfig.getString("facility-enum"),
    cacheConfig = facilityCache,
    dropBefore = dropBefore,
    timeout = timeoutMillis,
    enableTrace = enableTrace
  )

  private val medicalDepartmentCache: Option[QueryCacheConfig[FetchEnumQ, EnumAPI]] =
    cacheProvider.map(implicit x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[FetchEnumQ, EnumAPI](
          EntityKeyPrefix("medical-department-enum-client"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val medicalDepartmentModule: QueryModule[FetchEnumQ, EnumAPI] =
    QueryModule.make[FetchEnumQ, EnumAPI](
      uniqueTag = "medical-department-enum-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("medical-department-enum"),
      cacheConfig = medicalDepartmentCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  private val indicationsCache: Option[QueryCacheConfig[FetchEnumQ, EnumAPI]] =
    cacheProvider.map(implicit x =>
      QueryCacheConfig(
        alg = LiveCacheRedis.make[FetchEnumQ, EnumAPI](
          EntityKeyPrefix("treatment-indication-enum-client"),
          cacheProvider = x
        ),
        cleanOnLoad = persistClean
      )
    )

  val indicationsModule: QueryModule[FetchEnumQ, EnumAPI] =
    QueryModule.make[FetchEnumQ, EnumAPI](
      uniqueTag = "treatment-indication-enum-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("treatment-indication-enum"),
      cacheConfig = indicationsCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )

  private val therapyCache = cacheProvider.map(implicit x =>
    QueryCacheConfig(
      alg = LiveCacheRedis
        .make[FetchEnumQ, EnumAPI](EntityKeyPrefix("therapy-enum-client"), cacheProvider = x),
      cleanOnLoad = persistClean
    )
  )

  val therapyModule: QueryModule[FetchEnumQ, EnumAPI] = QueryModule.make[FetchEnumQ, EnumAPI](
    uniqueTag = "therapy-enum-client",
    sessionId = UUID.randomUUID(),
    targetTag = tagsConfig.getString("therapy-enum"),
    cacheConfig = therapyCache,
    dropBefore = dropBefore,
    timeout = timeoutMillis,
    enableTrace = enableTrace
  )

  private val priceUnitCache = cacheProvider.map(implicit x =>
    QueryCacheConfig(
      alg = LiveCacheRedis.make[FetchPriceVariantsQ, PriceUnitUI](
        EntityKeyPrefix("price-variant-client"),
        cacheProvider = x
      ),
      cleanOnLoad = persistClean
    )
  )

  val priceUnitModule: QueryModule[FetchPriceVariantsQ, PriceUnitUI] =
    QueryModule.make[FetchPriceVariantsQ, PriceUnitUI](
      uniqueTag = "price-variant-client",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("price-variant"),
      cacheConfig = priceUnitCache,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )
}

object QueryOperationClientModule {
  final def apply()(
      implicit bm: BaseModule,
      rm: RuntimeModule,
      am: AkkaModule,
      sem: StateEnvModule,
      ktm: KafkaTopicModule,
      kem: KafkaEdgeModule,
      cm: CacheModule
  ): QueryOperationClientModule = new QueryOperationClientModule
}
