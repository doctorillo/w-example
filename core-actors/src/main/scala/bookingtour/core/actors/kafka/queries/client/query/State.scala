package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.core.actors.kafka.queries.client.query.QueryCachedClient.QueryCacheConfig
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.{
  SessionCreate,
  SessionDelete,
  SessionQuery
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent._
import bookingtour.protocols.core.actors.operations.OpCommand.Start
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.messages.TaggedChannel.ChannelSession
import bookingtour.protocols.core.messages._
import bookingtour.protocols.core.register.RegisterEntity
import bookingtour.protocols.core.values.api.QueryResult
import cats.Order
import com.twitter.io.Buf
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait State[Query, Value] {
  _: Actor with ActorLogging with Timers =>
  val uniqueTag: String
  val sessionId: UUID
  val edgeRef: EdgeRef
  val targetTag: String
  val route: Bridge
  val cacheConfig: Option[QueryCacheConfig[Query, Value]]
  val dropBefore: Instant
  val enableTrace: Boolean

  val zioRuntime: zio.Runtime[zio.ZEnv]
  implicit val query0: Order[Query]
  implicit val resultEnc: Encoder[QueryResult[Value]]
  implicit val encoderQuery: Encoder[Query]
  implicit val decoderQuery: Decoder[Query]
  implicit val encoderValue: Encoder[Value]
  implicit val decoderValue: Decoder[Value]
  val sessionCreateEntity: RegisterEntity.Aux[SessionCreate]
  val sessionCreatedEntity: RegisterEntity.Aux[SessionCreated]
  val sessionDeleteEntity: RegisterEntity.Aux[SessionDelete]
  val sessionDeletedEntity: RegisterEntity.Aux[SessionDeleted]
  val sessionQueryEntity: RegisterEntity.Aux[SessionQuery[Query]]
  val sessionEmptyAnswerEntity: RegisterEntity.Aux[SessionEmptyReceived]
  val sessionStatusChangedEntity: RegisterEntity.Aux[SessionStatusChangedReceived]
  val sessionAnswerEntity: RegisterEntity.Aux[SessionAnswerReceived[Value]]

  private final val timerKey: UUID = UUID.randomUUID()

  override def preStart(): Unit =
    timers.startTimerAtFixedRate(timerKey, Start, 1.seconds)

  implicit protected final val taggedChannel: ChannelSession = ChannelSession(
    sessionId = sessionId,
    tag = targetTag
  )
  protected final val postOffice: PostOffice = PostOffice(uniqueTag)

  protected final def makeErrorAnswer(errors: List[String]): QueryResult[Value] = QueryResult(
    items = List.empty[Value],
    size = 0,
    hasError = true,
    debug = errors
  )

  protected final def makeErrorAnswerBuf(errors: List[String]): Buf =
    Buf.Utf8(
      QueryResult(
        items = List.empty[Value],
        size = 0,
        hasError = true,
        debug = errors
      ).asJson.noSpaces
    )

  protected final def switch(b: Receive, tag: String): Unit =
    context.become(b.orElse(unhandledBehavior(tag)))

  private final def unhandledBehavior(tag: String): Receive = {
    case msg =>
      log.error(s"$uniqueTag. $tag. unhandled message ${msg.getClass.getName}")
  }

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown.")
    }
    timers.cancel(timerKey)
    context.stop(self)
  }
}
