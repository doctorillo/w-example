package bookingtour.core.actors

import akka.event.LoggingAdapter
import bookingtour.protocols.core.actors._
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.{
  ChannelCreated,
  ChannelItemDeleted,
  ChannelItemUpdated,
  ChannelSnapshotReceived
}
import cats.Order
import cats.effect.{ContextShift, IO}
import cats.instances.int._
import cats.syntax.applicative._
import cats.syntax.order._
import zio._

package object primitives {
  final type DELETED[A]   = List[A]
  final type CHANGED[A]   = List[A]
  final type STATE[A]     = List[A]
  final type ON_UPDATE[A] = (DELETED[A], CHANGED[A], STATE[A])

  final def different[ID, VALUE](
      state: List[VALUE],
      received: List[VALUE]
  )(implicit stateR: VALUE => ID, idO: Order[ID], stateO: Order[VALUE]): Boolean = {
    val _received = received.distinct
    if (state.size =!= _received.size) {
      true
    } else {
      val _upserted = _received.exists { x =>
        val _id = stateR(x)
        state.exists(z => stateR(z) === _id && x =!= z) || !state.exists(z => stateR(z) === _id)
      }
      val _deleted = state.filterNot { x =>
        val _id = stateR(x)
        _received.exists(z => stateR(z) === _id)
      }.size > 0
      _upserted || _deleted
    }
  }

  final def diff[ID, VALUE](
      state: List[VALUE],
      received: List[VALUE]
  )(implicit outR: VALUE => ID, idO: Order[ID], outO: Order[VALUE]): ON_UPDATE[VALUE] = {
    val _received = received.distinct
    val _upserted = _received.filter { x =>
      val _id = outR(x)
      state.exists(z => outR(z) === _id && x =!= z) || !state.exists(z => outR(z) === _id)
    }
    val _deleted = state.filterNot { x =>
      val _id = outR(x)
      _received.exists(z => outR(z) === _id)
    }
    (_deleted, _upserted, received)
  }

  final def mkState[ID, VALUE](
      state: List[VALUE],
      received: List[VALUE]
  )(implicit outR: VALUE => ID, idO: Order[ID], outO: Order[VALUE]): ON_UPDATE[VALUE] = {
    val _received = received.distinct
    val _upserted = _received.filter { x =>
      val _id = outR(x)
      state.exists(z => outR(z) === _id && x =!= z) || !state.exists(z => outR(z) === _id)
    }
    val _deleted = state.filterNot { x =>
      val _id = outR(x)
      _received.exists(z => outR(z) === _id)
    }
    (_deleted, _upserted, _received)
  }

  final def mkStateToArrow[ID, VALUE](
      state: List[VALUE],
      received: List[VALUE]
  )(implicit outR: VALUE => ID, idO: Order[ID], outO: Order[VALUE]): ArrowResult[VALUE] = {
    val _received = received.distinct
    val _upserted = _received.filter { x =>
      val _id = outR(x)
      state.exists(z => outR(z) === _id && x =!= z) || !state.exists(z => outR(z) === _id)
    }
    val _deleted = state.filterNot { x =>
      val _id = outR(x)
      _received.exists(z => outR(z) === _id)
    }
    ArrowResult(state = _received, deleted = _deleted, upserted = _upserted)
  }

  final def mkStateCs[ID, VALUE](
      state: List[VALUE],
      received: List[VALUE]
  )(
      implicit cs: ContextShift[IO],
      outR: VALUE => ID,
      idO: Order[ID],
      outO: Order[VALUE]
  ): IO[ON_UPDATE[VALUE]] = {
    if (state.isEmpty) {
      (List.empty, received, received).pure[IO]
    } else {
      for {
        fiberR <- received.distinct.pure[IO].start
        fiberU <- received.filter { x =>
                   val _id = outR(x)
                   state.exists(z => outR(z) === _id && x =!= z) || !state.exists(z => outR(z) === _id)
                 }.pure[IO].start
        fiberD <- state.filterNot { x =>
                   val _id = outR(x)
                   received.exists(z => outR(z) === _id)
                 }.pure[IO].start
        _received <- fiberR.join
        _upserted <- fiberU.join
        _deleted  <- fiberD.join
      } yield {
        (_deleted, _upserted, _received)
      }
    }
  }

  final def zioMkState[ID, VALUE](
      state: List[VALUE],
      received: List[VALUE]
  )(
      implicit outR: VALUE => ID,
      idO: Order[ID],
      outO: Order[VALUE]
  ): UIO[ON_UPDATE[VALUE]] = {
    val zioReceived = ZIO.effectTotal(received.distinct)
    if (state.isEmpty) {
      zioReceived.map(x => (List.empty, x, x))
    } else {
      val zioUpserted = ZIO.effectTotal(
        received.filter { x =>
          val _id = outR(x)
          state.exists(z => outR(z) === _id && x =!= z) || !state.exists(z => outR(z) === _id)
        }
      )
      val zioDeleted = ZIO.effectTotal(
        state.filterNot { x =>
          val _id = outR(x)
          received.exists(z => outR(z) === _id)
        }
      )
      for {
        fiberR    <- zioReceived.fork
        fiberU    <- zioUpserted.fork
        fiberD    <- zioDeleted.fork
        _received <- fiberR.join
        _upserted <- fiberU.join
        _deleted  <- fiberD.join
      } yield {
        (_deleted, _upserted, _received)
      }
    }
  }

  final def onUpdate[ID, IN0, IN1, OUT](
      arrow: (List[OUT], List[IN0], List[IN1]) => List[OUT],
      enableTrace: Boolean
  )(ch0S: List[IN0], ch1S: List[IN1], outS: List[OUT])(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty) {
      val _outS: List[OUT] =
        arrow(
          outS,
          ch0S,
          ch1S
        ).distinct
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  final def onChannelUpdate[ID, IN0, IN1, OUT](
      arrow: (List[OUT], List[IN0], List[IN1]) => List[OUT],
      enableTrace: Boolean
  )(ch0S: List[IN0], ch1S: List[IN1], outS: List[OUT])(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty) {
      val _outS: List[OUT] =
        arrow(
          outS,
          ch0S,
          ch1S
        ).distinct
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  final def onChannelUpdate[ID, IN0, IN1, IN2, OUT](
      arrow: (List[OUT], List[IN0], List[IN1], List[IN2]) => List[OUT],
      enableTrace: Boolean
  )(ch0S: List[IN0], ch1S: List[IN1], ch2S: List[IN2], outS: List[OUT])(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty && ch2S.nonEmpty) {
      val _outS: List[OUT] = arrow(
        outS,
        ch0S,
        ch1S,
        ch2S
      ).distinct
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  final def onChannelUpdate[ID, IN0, IN1, IN2, IN3, OUT](
      arrow: (
          List[OUT],
          List[IN0],
          List[IN1],
          List[IN2],
          List[IN3]
      ) => List[OUT],
      enableTrace: Boolean
  )(
      ch0S: List[IN0],
      ch1S: List[IN1],
      ch2S: List[IN2],
      ch3S: List[IN3],
      outS: List[OUT]
  )(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty && ch2S.nonEmpty && ch3S.nonEmpty) {
      val _outS: List[OUT] = arrow(
        outS,
        ch0S,
        ch1S,
        ch2S,
        ch3S
      )
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  final def onChannelUpdate[ID, IN0, IN1, IN2, IN3, IN4, OUT](
      arrow: (
          List[OUT],
          List[IN0],
          List[IN1],
          List[IN2],
          List[IN3],
          List[IN4]
      ) => List[OUT],
      enableTrace: Boolean
  )(
      ch0S: List[IN0],
      ch1S: List[IN1],
      ch2S: List[IN2],
      ch3S: List[IN3],
      ch4S: List[IN4],
      outS: List[OUT]
  )(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty && ch2S.nonEmpty && ch3S.nonEmpty && ch4S.nonEmpty) {
      val _outS: List[OUT] = arrow(
        outS,
        ch0S,
        ch1S,
        ch2S,
        ch3S,
        ch4S
      )
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  final def onChannelUpdate[ID, IN0, IN1, IN2, IN3, IN4, IN5, OUT](
      arrow: (
          List[OUT],
          List[IN0],
          List[IN1],
          List[IN2],
          List[IN3],
          List[IN4],
          List[IN5]
      ) => List[OUT],
      enableTrace: Boolean
  )(
      ch0S: List[IN0],
      ch1S: List[IN1],
      ch2S: List[IN2],
      ch3S: List[IN3],
      ch4S: List[IN4],
      ch5S: List[IN5],
      outS: List[OUT]
  )(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty && ch2S.nonEmpty && ch3S.nonEmpty && ch4S.nonEmpty && ch5S.nonEmpty) {
      val _outS: List[OUT] = arrow(
        outS,
        ch0S,
        ch1S,
        ch2S,
        ch3S,
        ch4S,
        ch5S
      )
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  final def onChannelUpdate[ID, IN0, IN1, IN2, IN3, IN4, IN5, IN6, OUT](
      arrow: (
          List[OUT],
          List[IN0],
          List[IN1],
          List[IN2],
          List[IN3],
          List[IN4],
          List[IN5],
          List[IN6]
      ) => List[OUT],
      enableTrace: Boolean
  )(
      ch0S: List[IN0],
      ch1S: List[IN1],
      ch2S: List[IN2],
      ch3S: List[IN3],
      ch4S: List[IN4],
      ch5S: List[IN5],
      ch6S: List[IN6],
      outS: List[OUT]
  )(
      implicit outR: OUT => ID,
      idO: Order[ID],
      outO: Order[OUT]
  ): ON_UPDATE[OUT] = {
    if (ch0S.nonEmpty && ch1S.nonEmpty && ch2S.nonEmpty && ch3S.nonEmpty && ch4S.nonEmpty && ch5S.nonEmpty && ch6S.nonEmpty) {
      val _outS: List[OUT] = arrow(
        outS,
        ch0S,
        ch1S,
        ch2S,
        ch3S,
        ch4S,
        ch5S,
        ch6S
      )
      diff(outS, _outS)(outR, idO, outO)
    } else {
      if (outS.nonEmpty) {
        (outS, List.empty, List.empty)
      } else {
        (List.empty, List.empty, List.empty)
      }
    }
  }

  @deprecated
  final def onResult[OUT](
      tag: String,
      deleted: List[OUT],
      upserted: List[OUT],
      subscriptions: List[ChannelCreated]
  )(implicit log: LoggingAdapter): Unit = {
    if (deleted.nonEmpty) {
      for {
        s <- subscriptions.iterator
      } {
        log.info(
          s"$tag. send deleted ${s.tag}: ${deleted.length}."
        )
        s.consumer ! ChannelItemDeleted(s.channelId, deleted)
      }
    }
    if (upserted.nonEmpty) {
      for {
        s <- subscriptions.iterator
      } {
        log.info(
          s"$tag. send upserted ${s.tag}: ${upserted.length}."
        )
        s.consumer ! ChannelItemUpdated(s.channelId, upserted)
      }
    }
  }

  final def onSnapshot[OUT](
      tag: String,
      state: List[OUT],
      subscriptions: List[ChannelCreated]
  )(implicit log: LoggingAdapter): Unit = {
    if (state.nonEmpty) {
      for {
        s <- subscriptions.iterator
      } {
        log.info(
          s"$tag. send snapshot ${s.tag}: ${state.length}."
        )
        s.consumer ! ChannelSnapshotReceived(s.channelId, state)
      }
    }
  }
}
