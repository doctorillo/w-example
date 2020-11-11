package bookingtour.core.actors.primitives.channel.basic

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.collection.immutable.Map

import akka.actor.{Actor, ActorLogging, ActorRef}
import bookingtour.protocols.actors.aggregators._
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.{ChannelCreated, ChannelDeleted}
import bookingtour.protocols.core.actors.channels.query.ChannelFetchEvent.{AnswerReceived, EmptyReceived, ErrorReceived}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.{SignalChannelCreated, SignalChannelDeleted}
import bookingtour.protocols.core.actors.operations.OpCommand.{Completed, Stop}
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.Order
import zio.duration.Duration
import zio.{Cause, Exit, Task, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
protected[basic] trait State[Value, Id, PartitionKey] {
  _: Actor with ActorLogging =>

  protected val uniqueTag: String
  protected val manager: ActorRef
  protected val managerChannelId: UUID

  implicit protected val partitionFn: Value => PartitionKey
  implicit protected val zioRuntime: zio.Runtime[zio.ZEnv]
  implicit protected val ch0R: Value => Id
  implicit protected val chO: Order[Value]
  implicit protected val chIdO: Order[Id]
  implicit protected val keyO: Order[PartitionKey]

  protected val internalTimeoutSec: Long
  protected val enableTrace: Boolean

  private final val timeoutDuration: Duration = Duration(internalTimeoutSec, TimeUnit.SECONDS)

  protected final def replayMsg(
      channelId: UUID,
      replayTo: ActorRef,
      state: Map[PartitionKey, List[Value]]
  ): Unit = {
    if (state.isEmpty) {
      replayTo ! EmptyReceived(channelId)
    } else {
      val xs = state.toList.flatMap(_._2)
      replayTo ! AnswerReceived(channelId, xs)
    }
  }

  protected final def replayKeyFilteredMsg(
      channelId: UUID,
      replayTo: ActorRef,
      state: Map[PartitionKey, List[Value]],
      filter: Nothing => Boolean
  ): Unit = {
    if (state.isEmpty) {
      replayTo ! EmptyReceived(channelId)
    } else {
      val effect = for {
        a <- ZIO.effect(filter.asInstanceOf[PartitionKey => Boolean])
        b <- ZIO.effectTotal(state.filter(k => a(k._1)).flatMap(_._2).toList)
      } yield b
      zioRuntime.unsafeRunAsync(effect) {
        case Exit.Failure(cause) =>
          cause.failures
            .foreach(_ => log.error(s"$uniqueTag. replay-key-filtered-msg."))
          replayTo ! ErrorReceived(channelId, cause.failures)

        case Exit.Success(xs) if xs.isEmpty =>
          replayTo ! EmptyReceived(channelId)

        case Exit.Success(xs) =>
          replayTo ! AnswerReceived(channelId, xs)
      }
    }
  }

  protected final def replayValueFilteredMsg(
      channelId: UUID,
      replayTo: ActorRef,
      state: Map[PartitionKey, List[Value]],
      filter: Nothing => Boolean
  ): Unit = {
    if (state.isEmpty) {
      replayTo ! EmptyReceived(channelId)
    } else {
      val effect = for {
        a <- ZIO.effect(filter.asInstanceOf[Value => Boolean])
        b <- ZIO.effectTotal(state.values.flatMap(_.filter(a)).toList)
      } yield b
      zioRuntime.unsafeRunAsync(effect) {
        case Exit.Failure(cause) =>
          cause.failures
            .foreach(_ => log.error(s"$uniqueTag. replay-value-filtered-msg."))
          replayTo ! ErrorReceived(channelId, cause.failures)

        case Exit.Success(xs) if xs.isEmpty =>
          replayTo ! EmptyReceived(channelId)

        case Exit.Success(xs) =>
          replayTo ! AnswerReceived(channelId, xs)
      }
    }
  }

  protected final def replayKeyValueFilteredMsg(
      channelId: UUID,
      replayTo: ActorRef,
      state: Map[PartitionKey, List[Value]],
      keyCondition: Nothing => Boolean,
      valueCondition: Nothing => Boolean
  ): Unit = {
    if (state.isEmpty) {
      replayTo ! EmptyReceived(channelId)
    } else {
      val effect = for {
        ck <- ZIO.effect(keyCondition.asInstanceOf[PartitionKey => Boolean])
        cv <- ZIO.effect(valueCondition.asInstanceOf[Value => Boolean])
        a  <- ZIO.effectTotal(state.filter(x => ck(x._1)).flatMap(_._2.filter(cv)).toList)
      } yield a
      zioRuntime.unsafeRunAsync(effect) {
        case Exit.Failure(cause) =>
          cause.failures
            .foreach(_ => log.error(s"$uniqueTag. replay-key-value-filtered-msg."))
          replayTo ! ErrorReceived(channelId, cause.failures)

        case Exit.Success(xs) if xs.isEmpty =>
          replayTo ! EmptyReceived(channelId)

        case Exit.Success(xs) =>
          replayTo ! AnswerReceived(channelId, xs)
      }
    }
  }

  protected final def replayMapMsg(
      channelId: UUID,
      replayTo: ActorRef,
      state: Map[PartitionKey, List[Value]],
      map: Value => List[Any]
  ): Unit = {
    if (state.isEmpty) {
      replayTo ! EmptyReceived(channelId)
    } else {
      val effect = for {
        a <- ZIO.effect(map.asInstanceOf[Value => List[Value]])
        b <- ZIO.effectTotal(state.flatMap(_._2.flatMap(a)).toList)
      } yield b
      zioRuntime.unsafeRunAsync(effect) {
        case Exit.Failure(cause) =>
          cause.failures
            .foreach((thr: Throwable) => log.error(s"$uniqueTag. replay-map-msg. {}.", thr))
          replayTo ! ErrorReceived(channelId, cause.failures)

        case Exit.Success(xs) if xs.isEmpty =>
          replayTo ! EmptyReceived(channelId)

        case Exit.Success(xs) =>
          replayTo ! AnswerReceived(channelId, xs)
      }
    }
  }

  private final def makeEffectOnSnapshot(
      tag: String,
      sequenceId: SequenceNr,
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  ): Task[AggregatePartitionResult[PartitionKey, Value]] = {
    for {
      _ <- ZIO
            .effectTotal(log.info(s"$tag. sequence-id: $sequenceId. effect start."))
            .when(enableTrace)
      start  <- ZIO.effectTotal(System.currentTimeMillis())
      result <- snapshotToPartitionResult[Value, Id, PartitionKey](actual, received)
      finish <- ZIO.effectTotal(System.currentTimeMillis())
      _ <- ZIO
            .effect(
              log.info(
                s"$tag. sequence-id: $sequenceId. effect finish. result: ${result.state.nonEmpty}. job-time: ${Math
                  .round((finish - start) / 1000)} sec."
              )
            )
            .when(enableTrace)
    } yield result
  }

  private final def makeEffectOnCreate(
      tag: String,
      sequenceId: SequenceNr,
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  ): Task[AggregatePartitionResult[PartitionKey, Value]] = {
    for {
      _ <- ZIO
            .effectTotal(log.info(s"$tag. sequence-id: $sequenceId. effect start."))
            .when(enableTrace)
      start  <- ZIO.effectTotal(System.currentTimeMillis())
      result <- upsertToPartitionResult[Value, Id, PartitionKey](actual, received)
      finish <- ZIO.effectTotal(System.currentTimeMillis())
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$tag. sequence-id: $sequenceId. effect finish. result: ${result.state.nonEmpty}. job-time: ${Math
                  .round((finish - start) / 1000)} sec."
              )
            )
            .when(enableTrace)
    } yield result
  }

  private final def makeEffectOnUpdate(
      tag: String,
      sequenceId: SequenceNr,
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  ): Task[AggregatePartitionResult[PartitionKey, Value]] = {
    for {
      _ <- ZIO
            .effectTotal(log.info(s"$tag. sequence-id: $sequenceId. effect start."))
            .when(enableTrace)
      start <- ZIO.effectTotal(System.currentTimeMillis())
      result <- upsertToPartitionResult[Value, Id, PartitionKey](
                 actual = actual,
                 received = received
               )
      finish <- ZIO.effectTotal(System.currentTimeMillis())
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$tag. sequence-id: $sequenceId. effect finish. result: ${result.state.values.size}. job-time: ${Math
                  .round((finish - start) / 1000)} sec."
              )
            )
            .when(enableTrace)
    } yield result
  }

  private final def makeEffectOnDelete(
      tag: String,
      sequenceId: SequenceNr,
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  ): Task[AggregatePartitionResult[PartitionKey, Value]] = {
    for {
      _ <- ZIO
            .effectTotal(log.info(s"$tag. sequence-id: $sequenceId. effect start."))
            .when(enableTrace)
      start  <- ZIO.effectTotal(System.currentTimeMillis())
      result <- deletedToPartitionResult[Value, Id, PartitionKey](actual, received)
      finish <- ZIO.effectTotal(System.currentTimeMillis())
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$tag. sequence-id: $sequenceId. effect finish. result: ${result.state.nonEmpty}. job-time: ${Math
                  .round((finish - start) / 1000)} sec."
              )
            )
            .when(enableTrace)
    } yield result
  }

  private final def makeEffectOnDeleteId(
      tag: String,
      sequenceId: SequenceNr,
      actual: Map[PartitionKey, List[Value]],
      received: List[Id]
  ): Task[AggregatePartitionResult[PartitionKey, Value]] = {
    for {
      _ <- ZIO
            .effectTotal(log.info(s"$tag. sequence-id: $sequenceId. effect start."))
            .when(enableTrace)
      start  <- ZIO.effectTotal(System.currentTimeMillis())
      result <- deletedIdToPartitionResult[Value, Id, PartitionKey](actual, received)
      finish <- ZIO.effectTotal(System.currentTimeMillis())
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$tag. sequence-id: $sequenceId. effect finish. result: ${result.state.nonEmpty}. job-time: ${Math
                  .round((finish - start) / 1000)} sec."
              )
            )
            .when(enableTrace)
    } yield result
  }

  private final def cb(
      tag: String
  )(exit: Exit[Throwable, Option[AggregatePartitionResult[PartitionKey, Value]]]): Unit =
    exit match {
      case Exit.Failure(cause: Cause[Throwable]) =>
        cause.failures.foreach((thr: Throwable) => log.error(s"$tag. {}", thr))
        self ! Completed

      case Exit.Success(Some(value)) =>
        self ! value

      case Exit.Success(None) =>
        log.error(s"$tag. timeout received.")
        self ! Completed
    }

  protected final def onSnapshotEvent(
      tag: String,
      sequenceId: SequenceNr,
      received: List[Value]
  ): Map[PartitionKey, List[Value]] => Unit =
    actual =>
      zioRuntime.unsafeRunAsync(
        makeEffectOnSnapshot(tag, sequenceId, actual, received).timeout(timeoutDuration)
      )(cb(tag))

  protected final def onCreateEvent(
      tag: String,
      sequenceId: SequenceNr,
      created: List[Value]
  ): Map[PartitionKey, List[Value]] => Unit =
    actual =>
      zioRuntime.unsafeRunAsync(
        makeEffectOnCreate(tag, sequenceId, actual, created).timeout(timeoutDuration)
      )(cb(tag))

  protected final def onUpdateEvent(
      tag: String,
      sequenceId: SequenceNr,
      updated: List[Value]
  ): Map[PartitionKey, List[Value]] => Unit =
    actual =>
      zioRuntime.unsafeRunAsync(
        makeEffectOnUpdate(tag, sequenceId, actual, updated).timeout(timeoutDuration)
      )(cb(tag))

  protected final def onDeleteEvent(
      tag: String,
      sequenceId: SequenceNr,
      deleted: List[Value]
  ): Map[PartitionKey, List[Value]] => Unit =
    actual =>
      zioRuntime.unsafeRunAsync(
        makeEffectOnDelete(tag, sequenceId, actual, deleted).timeout(timeoutDuration)
      )(cb(tag))

  protected final def onDeleteIdEvent(
      tag: String,
      sequenceId: SequenceNr,
      deleted: List[Id]
  ): Map[PartitionKey, List[Value]] => Unit =
    actual =>
      zioRuntime.unsafeRunAsync(
        makeEffectOnDeleteId(tag, sequenceId, actual, deleted).timeout(timeoutDuration)
      )(cb(tag))

  protected final def defaultBehavior(
      consumers: List[ChannelCreated],
      signals: List[SignalChannelCreated]
  ): Receive = {
    case Stop =>
      if (enableTrace) {
        log.info(s"$uniqueTag. stop received.")
      }
      for {
        c <- consumers
      } yield c.consumer ! ChannelDeleted(c.channelId)
      for {
        c <- signals
      } yield c.consumer ! SignalChannelDeleted(c.channelId)
      shutdown()

    case msg =>
      log.error(s"$uniqueTag. basic-behavior. unhandled $msg")
      shutdown()
  }

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown")
    }
    context.stop(self)
  }
}
