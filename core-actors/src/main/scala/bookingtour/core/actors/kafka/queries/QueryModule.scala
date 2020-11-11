package bookingtour.core.actors.kafka.queries

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.extended.{ask => cargo}
import akka.util.Timeout
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.KafkaEdgeWrapper
import bookingtour.core.actors.kafka.queries.client.query.QueryCachedClient
import bookingtour.core.actors.kafka.queries.client.query.QueryCachedClient.QueryCacheConfig
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.{
  SessionCreate,
  SessionDelete,
  SessionQuery
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent._
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.messages.RunnableQuery
import bookingtour.protocols.core.register.RegisterEntity
import bookingtour.protocols.core.values.api.QueryResult
import cats.Order
import cats.effect.{ContextShift, IO}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
trait QueryModule[Query, Value] {
  val moduleId: UUID
  val clientRef: QueryCachedClient.QueryCachedClientRef[Query, Value]
  val timeoutMs: Long

  final def ask(query: Query)(implicit cs: ContextShift[IO]): IO[QueryResult[Value]] = {
    implicit val t: Timeout = Timeout.durationToTimeout(timeoutMs.millis)
    IO.fromFuture(IO {
      cargo(
        clientRef.x,
        (ref: ActorRef) =>
          RunnableQuery(
            query = query,
            expiredAt = Instant.now().plusMillis(timeoutMs),
            replayTo = ref
          )
      ).mapTo[QueryResult[Value]]
    })
  }
}

object QueryModule {
  final def make[Query, Value](
      uniqueTag: String,
      sessionId: UUID,
      targetTag: String,
      cacheConfig: Option[QueryCacheConfig[Query, Value]],
      dropBefore: Instant,
      timeout: Long,
      enableTrace: Boolean
  )(
      implicit r: Bridge,
      a: zio.Runtime[zio.ZEnv],
      i0: RegisterEntity.Aux[SessionCreate],
      i1: RegisterEntity.Aux[SessionCreated],
      i2: RegisterEntity.Aux[SessionDelete],
      i3: RegisterEntity.Aux[SessionDeleted],
      i4: RegisterEntity.Aux[SessionStatusChangedReceived],
      i5: RegisterEntity.Aux[SessionEmptyReceived],
      i6: RegisterEntity.Aux[SessionQuery[Query]],
      i7: RegisterEntity.Aux[SessionAnswerReceived[Value]],
      i8: Order[Query],
      i9: Encoder[Query],
      i10: Decoder[Query],
      i11: Encoder[Value],
      i12: Decoder[Value],
      e: KafkaEdgeWrapper,
      n: ActorSystem
  ): QueryModule[Query, Value] = {
    val ca: QueryCachedClient.QueryCachedClientRef[Query, Value] =
      QueryCachedClient.make[Query, Value](
        uniqueTag = uniqueTag,
        sessionId = sessionId,
        targetTag = targetTag,
        route = r,
        cacheConfig = cacheConfig,
        dropBefore = dropBefore,
        enableTrace = enableTrace
      )

    new QueryModule[Query, Value] {
      val moduleId: UUID                                                  = sessionId
      val clientRef: QueryCachedClient.QueryCachedClientRef[Query, Value] = ca
      val timeoutMs: Long                                                 = timeout
    }
  }
}
