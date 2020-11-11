package bookingtour.protocols.actors

import akka.event.LoggingAdapter
import bookingtour.protocols.actors.aggregators.AggregatePartitionResult.monoid._
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.{
  SignalChannelCreated,
  SignalChannelStatusChanged
}
import cats.Order
import cats.kernel.Monoid
import cats.syntax.eq._
import cats.syntax.monoid._
import zio.{UIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
package object aggregators {
  final type ZioResult[State] =
    ZIO[Any, String, AggregateResult[State]]

  private final def diffPartitionSnapshot[Value, Id, PartitionKey](
      key: PartitionKey,
      actual: List[Value],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    if (actual.isEmpty && received.isEmpty) {
      ZIO.effectTotal(
        AggregatePartitionResult[PartitionKey, Value](
          created = List.empty,
          updated = List.empty,
          deleted = List.empty,
          state = Map.empty
        )
      )
    } else if (actual.nonEmpty && received.isEmpty) {
      ZIO.effectTotal(
        AggregatePartitionResult[PartitionKey, Value](
          created = List.empty,
          updated = List.empty,
          deleted = actual,
          state = Map.empty
        )
      )
    } else {
      val c = received.filterNot { x =>
        val id = r(x)
        actual.exists(z => r(z) === id)
      }
      val u = received.filter { x =>
        val id = r(x)
        actual.exists(z => r(z) === id && z =!= x)
      }
      val d = actual.filterNot { x =>
        val id = r(x)
        received.exists(z => r(z) === id)
      }
      val s = actual.filterNot { x =>
        val id = r(x)
        u.exists(z => r(z) === id) || d.exists(z => r(z) === id)
      }
      val i = s ++ c ++ u
      val rs = if (i.isEmpty) {
        Map.empty[PartitionKey, List[Value]]
      } else {
        Map(key -> i)
      }
      ZIO.effectTotal(
        AggregatePartitionResult(
          created = c,
          updated = u,
          deleted = d,
          state = rs
        )
      )
    }
  }

  private final def diffPartitionUpsert[Value, Id, PartitionKey](
      key: PartitionKey,
      actual: List[Value],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    if (received.isEmpty) {
      ZIO.effectTotal(
        AggregatePartitionResult[PartitionKey, Value](
          created = List.empty,
          updated = List.empty,
          deleted = List.empty,
          state = Map(key -> actual)
        )
      )
    } else if (actual.isEmpty) {
      ZIO.effectTotal(
        AggregatePartitionResult[PartitionKey, Value](
          created = received,
          updated = List.empty,
          deleted = List.empty,
          state = Map(key -> received)
        )
      )
    } else {
      val c = received.filterNot { x =>
        val id = r(x)
        actual.exists(z => r(z) === id)
      }
      val u = received.filter { x =>
        val id = r(x)
        actual.exists(z => r(z) === id && z =!= x)
      }
      val s = actual.filterNot { x =>
        val id = r(x)
        u.exists(z => r(z) === id)
      }
      val i = s ++ c ++ u
      val rs = if (i.isEmpty) {
        Map.empty[PartitionKey, List[Value]]
      } else {
        Map(key -> i)
      }
      ZIO.effectTotal(
        AggregatePartitionResult(
          created = c,
          updated = u,
          deleted = List.empty,
          state = rs
        )
      )
    }
  }

  private final def diffPartitionDelete[Value, Id, PartitionKey](
      key: PartitionKey,
      actual: List[Value],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    if (received.isEmpty) {
      val rs = if (actual.isEmpty) {
        Map.empty[PartitionKey, List[Value]]
      } else {
        Map(key -> actual)
      }
      ZIO.effectTotal(
        AggregatePartitionResult[PartitionKey, Value](
          created = List.empty,
          updated = List.empty,
          deleted = List.empty,
          state = rs
        )
      )
    } else {
      val d = received.filter { x =>
        val id = r(x)
        actual.exists(z => r(z) === id)
      }
      val s = actual.filterNot { x =>
        val id = r(x)
        d.exists(z => r(z) === id)
      }
      val rs = if (s.isEmpty) {
        Map.empty[PartitionKey, List[Value]]
      } else {
        Map(key -> s)
      }
      ZIO.effectTotal(
        AggregatePartitionResult(
          created = List.empty,
          updated = List.empty,
          deleted = d,
          state = rs
        )
      )
    }
  }

  final def snapshotToResult[Value, Id](
      actual: List[Value],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregateResult[Value]] = {
    if (actual.isEmpty) {
      ZIO.effectTotal(
        AggregateResult(
          created = received,
          updated = List.empty,
          deleted = List.empty,
          state = received
        )
      )
    } else if (received.isEmpty) {
      ZIO.effectTotal(
        AggregateResult(
          created = List.empty,
          updated = List.empty,
          deleted = actual,
          state = List.empty
        )
      )
    } else {
      val d = actual.filterNot { x =>
        val id = r(x)
        received.exists(z => r(z) === id)
      }
      val c = received.filterNot { x =>
        val id = r(x)
        actual.exists(z => r(z) === id)
      }
      val u = received.filter { x =>
        val id = r(x)
        actual.exists(z => r(z) === id && z =!= x)
      }
      ZIO.effectTotal(
        AggregateResult(
          created = c,
          updated = u,
          deleted = d,
          state = received
        )
      )
    }
  }

  final def snapshotToPartitionResult[Value, Id, PartitionKey](
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      o3: Order[PartitionKey],
      r: Value => Id,
      partitionFn: Value => PartitionKey
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    val r2m = received.groupBy(partitionFn)
    val kxs = (actual.keys ++ r2m.keys).toList.distinct
    val effects = for {
      key <- kxs
      axs = actual.getOrElse(key, List.empty)
      rxs = r2m.getOrElse(key, List.empty)
    } yield diffPartitionSnapshot[Value, Id, PartitionKey](
      key = key,
      actual = axs,
      received = rxs
    )
    ZIO
      .collectAll(effects)
      .map(
        _.foldLeft(Monoid.empty[AggregatePartitionResult[PartitionKey, Value]])((acc, x) => acc |+| x)
      )
  }

  final def upsertToResult[Value, Id](
      actual: List[Value],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregateResult[Value]] = {
    if (actual.isEmpty) {
      ZIO.effectTotal(
        AggregateResult(
          created = received,
          updated = List.empty,
          deleted = List.empty,
          state = received
        )
      )
    } else {
      val c = received.filterNot { x =>
        val id = r(x)
        actual.exists(z => r(z) === id)
      }
      val u = received.filter { x =>
        val id = r(x)
        actual.exists(z => r(z) === id && z =!= x)
      }
      val s = actual.filterNot { x =>
        val id = r(x)
        u.exists(z => r(z) === id)
      }
      ZIO.effectTotal(
        AggregateResult(
          created = c,
          updated = u,
          deleted = List.empty,
          state = s ++ c ++ u
        )
      )
    }
  }

  final def upsertToPartitionResult[Value, Id, PartitionKey](
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      o3: Order[PartitionKey],
      r: Value => Id,
      partitionFn: Value => PartitionKey
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    val groupedReceived = received.groupBy(partitionFn)
    val orphanActual    = actual.filterNot(x => groupedReceived.exists(_._1 === x._1))
    val keys = groupedReceived.map {
      case (k, xs) =>
        diffPartitionUpsert[Value, Id, PartitionKey](
          key = k,
          actual = actual.getOrElse(k, List.empty),
          received = xs
        )
    }
    ZIO
      .collectAll(keys)
      .map(
        _.foldLeft(
          AggregatePartitionResult(
            created = List.empty,
            updated = List.empty,
            deleted = List.empty,
            state = orphanActual
          )
        )((acc, x) => acc |+| x)
      )
  }

  final def deletedToResult[Value, Id](
      actual: List[Value],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregateResult[Value]] = {
    if (actual.isEmpty) {
      ZIO.effectTotal(AggregateResult.empty[Value])
    } else {
      val d = actual.filter { x =>
        val id = r(x)
        received.exists(z => r(z) === id)
      }
      val s = actual.filterNot { x =>
        val id = r(x)
        d.exists(z => r(z) === id)
      }
      ZIO.effectTotal(
        AggregateResult(
          created = List.empty,
          updated = List.empty,
          deleted = d,
          state = s
        )
      )
    }
  }

  final def deletedToPartitionResult[Value, Id, PartitionKey](
      actual: Map[PartitionKey, List[Value]],
      received: List[Value]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      o3: Order[PartitionKey],
      r: Value => Id,
      partitionFn: Value => PartitionKey
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    val groupedReceived = received.groupBy(partitionFn)
    val orphanActual    = actual.filterNot(x => groupedReceived.exists(_._1 === x._1))
    val keys = groupedReceived.map {
      case (k, xs) =>
        diffPartitionDelete[Value, Id, PartitionKey](
          k,
          actual.getOrElse(k, List.empty),
          xs
        )
    }
    ZIO
      .collectAll(keys)
      .map(
        _.foldLeft(
          AggregatePartitionResult(
            created = List.empty,
            updated = List.empty,
            deleted = List.empty,
            state = orphanActual
          )
        )((acc, x) => acc |+| x)
      )
  }

  final def deletedIdToResult[Value, Id](
      actual: List[Value],
      received: List[Id]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      r: Value => Id
  ): UIO[AggregateResult[Value]] = {
    val d = actual.filter(x => received.exists(_ === r(x)))
    deletedToResult[Value, Id](actual, d)
  }

  final def deletedIdToPartitionResult[Value, Id, PartitionKey](
      actual: Map[PartitionKey, List[Value]],
      received: List[Id]
  )(
      implicit o1: Order[Value],
      o2: Order[Id],
      o3: Order[PartitionKey],
      r: Value => Id,
      partitionFn: Value => PartitionKey
  ): UIO[AggregatePartitionResult[PartitionKey, Value]] = {
    val d = actual.foldLeft(List.empty[Value])((acc, x) => acc ++ x._2.filter(z => received.exists(_ === r(z))))
    deletedToPartitionResult[Value, Id, PartitionKey](actual, d)
  }

  final def sendToChannel[Value](
      uniqueTag: String,
      log: LoggingAdapter,
      enableTrace: Boolean,
      channel: ChannelCreated,
      state: List[Value]
  ): Unit = {
    if (enableTrace) {
      log.info(
        s"$uniqueTag. send-to-channel. tag: ${channel.tag}. state: ${state.length}. complete."
      )
    }
    if (state.isEmpty) {
      channel.consumer ! ChannelEmptySnapshotReceived(channel.channelId)
    } else {
      channel.consumer ! ChannelSnapshotReceived(channel.channelId, state)
    }
  }

  final def sendToConsumer[Key, Value](
      uniqueTag: String,
      log: LoggingAdapter,
      enableTrace: Boolean,
      consumers: List[ChannelCreated],
      value: AggregatePartitionResult[Key, Value]
  ): Unit = {
    if (consumers.nonEmpty) {
      val AggregatePartitionResult(created, updated, deleted, _) = value
      for {
        ch <- consumers
      } {
        if (deleted.nonEmpty) {
          ch.consumer ! ChannelItemDeleted(ch.channelId, deleted)
          if (enableTrace) {
            log.info(
              s"$uniqueTag. send-to-consumer. tag: ${ch.tag}. deleted ${deleted.size} items."
            )
          }
        }
        if (created.nonEmpty) {
          ch.consumer ! ChannelItemCreated(ch.channelId, created)
          if (enableTrace) {
            log.info(
              s"$uniqueTag. send-to-consumer. tag: ${ch.tag}. created ${created.size} items."
            )
          }
        }
        if (updated.nonEmpty) {
          ch.consumer ! ChannelItemUpdated(ch.channelId, updated)
          if (enableTrace) {
            log.info(
              s"$uniqueTag. send-to-consumer. tag: ${ch.tag}. updated ${updated.size} items."
            )
          }
        }
      }
    }
  }

  final def sendStatus(
      uniqueTag: String,
      log: LoggingAdapter,
      enableTrace: Boolean,
      consumers: List[ChannelCreated],
      signals: List[SignalChannelCreated],
      status: ChannelStatus
  ): Unit = {
    if (enableTrace) {
      log.info(
        s"$uniqueTag. status send. status: $status. consumers: ${consumers.length}. signals: ${signals.length}."
      )
    }
    for {
      ch <- consumers
      _ = if (enableTrace) {
        log.info(s"$uniqueTag. status send. to ${ch.tag}.")
      }
    } yield ch.consumer ! ChannelStatusChanged(ch.channelId, status)
    for {
      ch <- signals
      _ = if (enableTrace) {
        log.info(s"$uniqueTag. status send. to ${ch.tag}.")
      }
    } yield ch.consumer ! SignalChannelStatusChanged(ch.channelId, status)
  }
}
