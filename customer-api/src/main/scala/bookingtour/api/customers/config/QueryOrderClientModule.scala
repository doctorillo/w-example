package bookingtour.api.customers.config

import java.time.Instant
import java.util.UUID

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.actors.modules.{AkkaModule, KafkaEdgeModule, KafkaTopicModule, StateEnvModule}
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.cmd.UpsertPlannerSessionQ
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2020.
  */
final class QueryOrderClientModule private (
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
  import kem._
  import ktm._
  import rm._

  private val tagsConfig: Config = appConfig.getConfig("tagged-channel.orders")
  implicit private val route: Bridge =
    Bridge(output = topics.ordersTopic, input = topics.apiCustomerTopic)
  private val timeoutMillis = 3000L
  private val dropBefore    = Instant.now().minusSeconds(60L)

  val plannerModifyModule: QueryModule[UpsertPlannerSessionQ, PlannerSession] =
    QueryModule.make[UpsertPlannerSessionQ, PlannerSession](
      uniqueTag = "planner-session-modify",
      sessionId = UUID.randomUUID(),
      targetTag = tagsConfig.getString("planner-session-modify"),
      cacheConfig = None,
      dropBefore = dropBefore,
      timeout = timeoutMillis,
      enableTrace = enableTrace
    )
}

object QueryOrderClientModule {
  final def apply()(
      implicit bm: BaseModule,
      rm: RuntimeModule,
      am: AkkaModule,
      sem: StateEnvModule,
      ktm: KafkaTopicModule,
      kem: KafkaEdgeModule,
      cm: CacheModule
  ): QueryOrderClientModule = new QueryOrderClientModule
}
