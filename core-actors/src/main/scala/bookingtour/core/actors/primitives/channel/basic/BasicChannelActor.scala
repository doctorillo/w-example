package bookingtour.core.actors.primitives.channel.basic

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.channels.MakeChannel
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.Order
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class BasicChannelActor[Value, Id, PartitionKey] private (
    val uniqueTag: String,
    val manager: ActorRef,
    val managerChannelId: UUID,
    val internalTimeoutSec: Long,
    val enableTrace: Boolean
)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv],
    val ch0R: Value => Id,
    val chO: Order[Value],
    val chIdO: Order[Id],
    val keyO: Order[PartitionKey],
    val partitionFn: Value => PartitionKey
) extends Actor with ActorLogging with State[Value, Id, PartitionKey] with SignalBehavior[Value, Id, PartitionKey]
    with ConsumerBehavior[Value, Id, PartitionKey] with QueryBehavior[Value, Id, PartitionKey]
    with BasicBehavior[Value, Id, PartitionKey] {
  override def preStart(): Unit = {
    super.preStart()
    basicBehavior(
      sequenceId = SequenceNr.Zero,
      consumers = List.empty,
      pendingConsumers = List.empty,
      signals = List.empty,
      state = Map.empty,
      publishedStatus = ChannelStatus.Undefined,
      pendingStatus = None,
      pending = List.empty,
      running = false
    )
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. re-start. {}", reason)
    shutdown()
  }

  def receive: Receive = Actor.emptyBehavior
}

object BasicChannelActor {
  implicit final class MapOps[A, B](private val self: Map[A, List[B]]) extends AnyVal {
    def length: Long = self.foldLeft(0L)((acc, xs) => acc + xs._2.length)
  }

  final def makeChannel[Value, Id, PartitionKey](
      internalTimeoutSec: Long = 180L,
      trace: Boolean
  )(
      implicit zioRuntime: zio.Runtime[zio.ZEnv],
      ch0R: Value => Id,
      ch0O: Order[Value],
      ch0IdO: Order[Id],
      keyO: Order[PartitionKey],
      partitionFn: Value => PartitionKey
  ): MakeChannel[Value, Id] =
    new MakeChannel[Value, Id] {
      val enableTrace: Boolean = trace

      def make(
          ctx: ActorContext,
          managerRef: ActorRef,
          managerChannelId: UUID,
          uniqueTag: String
      ): ActorProducer[Value, Id] =
        ctx
          .actorOf(
            Props(
              new BasicChannelActor[Value, Id, PartitionKey](
                uniqueTag = uniqueTag,
                manager = managerRef,
                managerChannelId = managerChannelId,
                internalTimeoutSec = internalTimeoutSec,
                enableTrace = enableTrace
              )
            ),
            uniqueTag
          )
          .coerce[ActorProducer[Value, Id]]
    }
}
