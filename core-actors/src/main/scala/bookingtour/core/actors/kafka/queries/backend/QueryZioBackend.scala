package bookingtour.core.actors.kafka.queries.backend

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash, Timers}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.{EdgeRef, KafkaEdgeWrapper}
import bookingtour.core.actors.primitives.channel.accumulate.AccumulatorChangeActor.AccumulatorChangeActorRef
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.{
  SessionCreate,
  SessionDelete,
  SessionQuery
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent._
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.register.RegisterEntity
import cats.Order
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import zio.URIO

/**
  * © Alexey Toroshchin 2019.
  */
final class QueryZioBackend[R, Query, Answer] private (
    val uniqueTag: String,
    val stateRef: AccumulatorChangeActorRef,
    val targetTag: String,
    val route: Bridge,
    val zioEnv: R,
    val queryEffect: Query => URIO[R, List[Answer]],
    val consumerPoolRef: EdgeRef,
    val ttlMs: Long,
    val enableTrace: Boolean
)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv],
    val sessionCreatedO: Order[SessionCreated],
    val sessionCreateEntity: RegisterEntity.Aux[SessionCreate],
    val sessionCreatedEntity: RegisterEntity.Aux[SessionCreated],
    val sessionDeleteEntity: RegisterEntity.Aux[SessionDelete],
    val sessionDeletedEntity: RegisterEntity.Aux[SessionDeleted],
    val sessionQueryEntity: RegisterEntity.Aux[SessionQuery[Query]],
    val sessionStatusChangedEntity: RegisterEntity.Aux[SessionStatusChangedReceived],
    val sessionEmptyAnswerEntity: RegisterEntity.Aux[SessionEmptyReceived],
    val sessionAnswerEntity: RegisterEntity.Aux[SessionAnswerReceived[Answer]]
) extends Actor with Stash with Timers with ActorLogging with State[R, Query, Answer]
    with SignalBehavior[R, Query, Answer] with CreateBehavior[R, Query, Answer] with CreatedBehavior[R, Query, Answer]
    with DeleteBehavior[R, Query, Answer] with DeletedBehavior[R, Query, Answer] with QueryBehavior[R, Query, Answer]
    with StatusChangedBehavior[R, Query, Answer] with EmptyAnswerBehavior[R, Query, Answer]
    with AnswerBehavior[R, Query, Answer] with BasicBehavior[R, Query, Answer] {
  override def preStart(): Unit = {
    super.preStart()
    if (enableTrace) {
      log.info(s"$uniqueTag. session-query type: ${sessionQueryEntity.key.typeTag}.")
    }
    signalBehavior()
  }
}

object QueryZioBackend {
  import scala.reflect.runtime.universe._

  @newtype final case class QueryZioBackendRef[R, Query, Answer](x: ActorRef)

  final def make[R, Query: Encoder: Decoder: WeakTypeTag, Answer: Encoder: Decoder: WeakTypeTag](
      uniqueTag: String,
      stateRef: AccumulatorChangeActorRef,
      targetTag: String,
      inputTopic: String,
      outputTopic: String,
      zioEnv: R,
      queryEffect: Query => URIO[R, List[Answer]],
      ttl: Long,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      consumerPool: KafkaEdgeWrapper
  ): QueryZioBackendRef[R, Query, Answer] = {
    implicit val a: RegisterEntity.Aux[SessionCreate]  = RegisterEntity()
    implicit val b: RegisterEntity.Aux[SessionCreated] = RegisterEntity()
    implicit val c: RegisterEntity.Aux[SessionDelete]  = RegisterEntity()
    implicit val d: RegisterEntity.Aux[SessionDeleted] = RegisterEntity()
    implicit val e: RegisterEntity.Aux[SessionQuery[Query]] =
      RegisterEntity[SessionQuery[Query]]()
    implicit val f: RegisterEntity.Aux[SessionStatusChangedReceived] = RegisterEntity()
    implicit val g: RegisterEntity.Aux[SessionEmptyReceived]         = RegisterEntity()
    implicit val h: RegisterEntity.Aux[SessionAnswerReceived[Answer]] =
      RegisterEntity[SessionAnswerReceived[Answer]]()
    ctx
      .actorOf(
        props = Props(
          new QueryZioBackend[R, Query, Answer](
            uniqueTag = uniqueTag,
            stateRef = stateRef,
            targetTag = targetTag,
            consumerPoolRef = consumerPool.pool,
            route = Bridge(output = outputTopic, input = inputTopic),
            zioEnv = zioEnv,
            queryEffect = queryEffect,
            ttlMs = ttl,
            enableTrace = enableTrace
          )
        ),
        name = uniqueTag
      )
      .coerce[QueryZioBackendRef[R, Query, Answer]]
  }
}
