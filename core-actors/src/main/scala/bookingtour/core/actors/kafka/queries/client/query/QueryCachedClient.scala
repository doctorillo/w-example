package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash, Timers}
import bookingtour.core.actors.cache.AlgCache
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.KafkaEdgeWrapper
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.{EdgeRef, KafkaEdgeWrapper}
import bookingtour.core.actors.kafka.queries.client.query.QueryCachedClient.QueryCacheConfig
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.{
  SessionCreate,
  SessionDelete,
  SessionQuery
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent._
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.register.RegisterEntity
import bookingtour.protocols.core.values.api.QueryResult
import cats.Order
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class QueryCachedClient[Query, Value] private (
    val uniqueTag: String,
    val sessionId: UUID,
    val edgeRef: EdgeRef,
    val targetTag: String,
    val route: Bridge,
    val cacheConfig: Option[QueryCacheConfig[Query, Value]],
    val dropBefore: Instant,
    val enableTrace: Boolean
)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv],
    val query0: Order[Query],
    val encoderQuery: Encoder[Query],
    val decoderQuery: Decoder[Query],
    val encoderValue: Encoder[Value],
    val decoderValue: Decoder[Value],
    val resultEnc: Encoder[QueryResult[Value]],
    val sessionCreateEntity: RegisterEntity.Aux[SessionCreate],
    val sessionCreatedEntity: RegisterEntity.Aux[SessionCreated],
    val sessionDeleteEntity: RegisterEntity.Aux[SessionDelete],
    val sessionDeletedEntity: RegisterEntity.Aux[SessionDeleted],
    val sessionQueryEntity: RegisterEntity.Aux[SessionQuery[Query]],
    val sessionStatusChangedEntity: RegisterEntity.Aux[SessionStatusChangedReceived],
    val sessionEmptyAnswerEntity: RegisterEntity.Aux[SessionEmptyReceived],
    val sessionAnswerEntity: RegisterEntity.Aux[SessionAnswerReceived[Value]]
) extends Actor with Stash with Timers with ActorLogging with State[Query, Value] with CreateBehavior[Query, Value]
    with CreatedBehavior[Query, Value] with DeleteBehavior[Query, Value] with DeletedBehavior[Query, Value]
    with QueryBehavior[Query, Value] with EmptyAnswerBehavior[Query, Value] with AnswerBehavior[Query, Value]
    with StatusChangedBehavior[Query, Value] with BasicBehavior[Query, Value] {
  override def preStart(): Unit = {
    super.preStart()
    if (enableTrace) {
      log.info(s"$uniqueTag. query type: ${sessionQueryEntity.key.typeTag}")
    }
    createBehavior()
  }
  override def receive: Receive = Actor.emptyBehavior
}

object QueryCachedClient {
  final case class QueryCacheConfig[Query, Value](
      alg: AlgCache.Aux[Any, Query, Value],
      cleanOnLoad: Boolean
  )

  @newtype final case class QueryCachedClientRef[Query, Value](x: ActorRef)

  final def make[Query, Value](
      uniqueTag: String,
      sessionId: UUID,
      targetTag: String,
      route: Bridge,
      cacheConfig: Option[QueryCacheConfig[Query, Value]],
      dropBefore: Instant,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      poolWrapper: KafkaEdgeWrapper,
      query0: Order[Query],
      encoderQuery: Encoder[Query],
      decoderQuery: Decoder[Query],
      encoderValue: Encoder[Value],
      decoderValue: Decoder[Value],
      sessionCreateEntity: RegisterEntity.Aux[SessionCreate],
      sessionCreatedEntity: RegisterEntity.Aux[SessionCreated],
      sessionDeleteEntity: RegisterEntity.Aux[SessionDelete],
      sessionDeletedEntity: RegisterEntity.Aux[SessionDeleted],
      sessionQueryEntity: RegisterEntity.Aux[SessionQuery[Query]],
      sessionStatusChangedEntity: RegisterEntity.Aux[SessionStatusChangedReceived],
      sessionEmptyAnswerEntity: RegisterEntity.Aux[SessionEmptyReceived],
      sessionAnswerEntity: RegisterEntity.Aux[SessionAnswerReceived[Value]]
  ): QueryCachedClientRef[Query, Value] =
    ctx
      .actorOf(
        Props(
          new QueryCachedClient[Query, Value](
            uniqueTag = uniqueTag,
            sessionId = sessionId,
            edgeRef = poolWrapper.pool,
            targetTag = targetTag,
            route = route,
            cacheConfig = cacheConfig,
            dropBefore = dropBefore,
            enableTrace = enableTrace
          )
        )
      )
      .coerce[QueryCachedClientRef[Query, Value]]
}
