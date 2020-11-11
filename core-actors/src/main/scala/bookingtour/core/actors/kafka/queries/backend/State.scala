package bookingtour.core.actors.kafka.queries.backend

import java.time.Instant

import akka.actor.{Actor, ActorLogging, ActorRef}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.core.actors.primitives.channel.accumulate.AccumulatorChangeActor.AccumulatorChangeActorRef
import bookingtour.core.actors.primitives.channel.accumulate.AccumulatorChangeActor.AccumulatorChangeActorRef
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.{
  SessionCreate,
  SessionDelete,
  SessionQuery
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent._
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.messages.TaggedChannel.ChannelTag
import bookingtour.protocols.core.messages._
import bookingtour.protocols.core.register.RegisterEntity
import cats.Order
import zio.{Exit, URIO}

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait State[R, Query, Answer] {
  _: Actor with ActorLogging =>

  final type State = (SessionCreated, ActorRef)

  val uniqueTag: String
  val stateRef: AccumulatorChangeActorRef
  val consumerPoolRef: EdgeRef
  val targetTag: String
  val route: Bridge
  val queryEffect: Query => URIO[R, List[Answer]]
  val ttlMs: Long
  val enableTrace: Boolean

  implicit val zioRuntime: zio.Runtime[zio.ZEnv]
  implicit val zioEnv: R
  implicit val sessionCreatedO: Order[SessionCreated]
  val sessionCreateEntity: RegisterEntity.Aux[SessionCreate]
  val sessionCreatedEntity: RegisterEntity.Aux[SessionCreated]
  val sessionDeleteEntity: RegisterEntity.Aux[SessionDelete]
  val sessionDeletedEntity: RegisterEntity.Aux[SessionDeleted]
  val sessionQueryEntity: RegisterEntity.Aux[SessionQuery[Query]]
  val sessionStatusChangedEntity: RegisterEntity.Aux[SessionStatusChangedReceived]
  val sessionEmptyAnswerEntity: RegisterEntity.Aux[SessionEmptyReceived]
  val sessionAnswerEntity: RegisterEntity.Aux[SessionAnswerReceived[Answer]]

  protected val dropBefore: Instant                   = Instant.now()
  implicit protected final val postOffice: PostOffice = PostOffice(uniqueTag)

  implicit protected final val taggedChannel: ChannelTag = ChannelTag(tag = targetTag)

  protected final def runQuery(sessionQuery: Query)(
      cb: Either[List[String], List[Answer]] => Unit
  ): Unit = {
    zioRuntime.unsafeRunAsync(queryEffect(sessionQuery).provide(zioEnv)) {
      case Exit.Failure(cause) =>
        cb(Left(cause.failures))

      case Exit.Success(chain) =>
        cb(Right(chain))
    }
  }

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
    context.stop(self)
  }

  override def receive: Receive = Actor.emptyBehavior
}
