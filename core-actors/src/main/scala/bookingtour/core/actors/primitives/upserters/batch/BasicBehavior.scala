package bookingtour.core.actors.primitives.upserters.batch

import scala.collection.immutable.List

import akka.actor.{Actor, ActorLogging}
import bookingtour.core.actors.primitives._
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.{
  SignalChannelCreated,
  SignalChannelStatusChanged
}
import bookingtour.protocols.core.actors.internal.InternalCommand.{AttachErrorId, DetachId}
import bookingtour.protocols.core.actors.operations.OpCommand.Completed
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[batch] trait BasicBehavior[Value, ValueId] {
  _: Actor with ActorLogging with State[Value, ValueId] =>

  private final def startProcessing(
      channel: ChannelCreated,
      signals: List[SignalChannelCreated],
      upserted: List[Value],
      pending: List[Value],
      error: List[Value],
      publishedStatus: ChannelStatus,
      running: Boolean
  ): Unit = {
    if (!running && error.nonEmpty) {
      for {
        h <- error.headOption
        t = error.tail
      } {
        if (enableTrace) {
          log.info(
            s"$uniqueTag. error pruned run-upsert. start. upserted: ${upserted.length}. error: ${t.length}."
          )
        }
        val st = System.currentTimeMillis()
        basicBehavior(
          channel = channel,
          signals = signals,
          upserted = upserted :+ h,
          pending = pending,
          error = t,
          publishedStatus = publishedStatus,
          running = true
        )
        runUpsert.run(List(h)) {
          case Left(thr) =>
            log.error(s"$uniqueTag. error pruned run-upsert. {}", thr)
            self ! Completed

          case Right(_) =>
            if (enableTrace) {
              log.info(
                s"$uniqueTag. error pruned run-upsert. complete. job-time: ${System.currentTimeMillis() - st} ms."
              )
            }
            self ! Completed
        }
      }
    } else if (!running && pending.nonEmpty) {
      val b        = pending.grouped(batchSize).toList
      val _upsert  = b.head.distinct
      val _pending = b.tail.flatten
      if (enableTrace) {
        log.info(
          s"$uniqueTag. run-upsert. start. upserted: ${upserted.length}. upsert: ${_upsert.length}. pending: ${_pending.length}. error: ${error.length}."
        )
      }
      basicBehavior(
        channel = channel,
        signals = signals,
        upserted = upserted ++ _upsert,
        pending = _pending,
        error = error,
        publishedStatus = publishedStatus,
        running = true
      )
      val st = System.currentTimeMillis()
      runUpsert.run(_upsert) {
        case Left(thr) =>
          log.error(s"$uniqueTag. run-upsert. {}", thr)
          self ! DetachId(_upsert)
          self ! Completed
          self ! AttachErrorId(_upsert)

        case Right(_) =>
          if (enableTrace) {
            log.info(
              s"$uniqueTag. run-upsert. complete. job-time: ${System.currentTimeMillis() - st} ms."
            )
          }
          self ! Completed
      }
    } else {
      basicBehavior(
        channel = channel,
        signals = signals,
        upserted = upserted,
        pending = pending,
        error = error,
        publishedStatus = publishedStatus,
        running = running
      )
    }
  }

  private final def behaviors(
      channel: ChannelCreated,
      signals: List[SignalChannelCreated],
      upserted: List[Value],
      pending: List[Value],
      error: List[Value],
      publishedStatus: ChannelStatus,
      running: Boolean
  ): Receive = {
    case msg: SignalChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-created received. tag: ${msg.tag}. status: $publishedStatus. pending:${pending.length}. error: ${error.length}."
        )
      }
      val s = SignalChannelCreated(msg.channelId, msg.tag, self, msg.consumer)
      startProcessing(
        channel = channel,
        signals = signals :+ s,
        upserted = upserted,
        pending = pending,
        error = error,
        publishedStatus = publishedStatus,
        running = running
      )
      msg.consumer ! s
      msg.consumer ! SignalChannelStatusChanged(msg.channelId, publishedStatus)

    case Completed =>
      val status: ChannelStatus = if (pending.nonEmpty || error.nonEmpty) {
        ChannelStatus.Busy
      } else if (upserted.nonEmpty && pending.isEmpty && error.isEmpty) {
        ChannelStatus.Ready
      } else {
        ChannelStatus.Undefined
      }
      if (enableTrace) {
        log.info(
          s"$uniqueTag. stop-job. upserted: ${upserted.length}. pending:${pending.length}. error: ${error.length}. published-status: $publishedStatus. calculated-status: $status."
        )
      }
      if (status =!= publishedStatus) {
        signals.foreach { x =>
          if (enableTrace) {
            log.info(
              s"$uniqueTag. send calculated-status: $status."
            )
          }
          x.consumer ! SignalChannelStatusChanged(x.channelId, status)
        }
      }
      startProcessing(
        channel = channel,
        signals = signals,
        upserted = upserted,
        pending = pending,
        error = error,
        publishedStatus = publishedStatus,
        running = false
      )

    case DetachId(xs) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. detach-id. data: ${xs.length} items."
        )
      }
      val _xs = xs.asInstanceOf[List[Value]]
      startProcessing(
        channel = channel,
        signals = signals,
        upserted = upserted.filterNot { x =>
          val _id = paramR(x)
          _xs.exists(z => paramR(z) === _id)
        },
        pending = pending,
        error = error,
        publishedStatus = publishedStatus,
        running = running
      )

    case AttachErrorId(xs) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. attach-error-id. data: ${xs.length} items."
        )
      }
      val _xs = xs.asInstanceOf[List[Value]]
      val _error = error.filterNot { x =>
        val _id = paramR(x)
        _xs.exists(z => paramR(z) === _id)
      } ++ _xs
      startProcessing(
        channel = channel,
        signals = signals,
        upserted = upserted,
        pending = pending,
        error = _error.distinct,
        publishedStatus = publishedStatus,
        running = running
      )

    case ChannelSnapshotReceived(id, nec) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. ch: params. channel-snapshot-received. data: ${nec.length} items. params: ${upserted.length}."
        )
      }
      val _nec = nec.asInstanceOf[List[Value]]
      if (different(upserted, _nec)(paramR, paramIdO, paramO)) {
        if (enableTrace) {
          log.info(
            s"$uniqueTag. ch: params. channel-snapshot-received. param: ${_nec.length} items."
          )
        }
        startProcessing(
          channel = channel,
          signals = signals,
          upserted = List.empty,
          pending = _nec,
          error = List.empty,
          publishedStatus = publishedStatus,
          running = running
        )
      }

    case ChannelEmptySnapshotReceived(id) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. ch: params. channel-empty-snapshot-received."
        )
      }

    case ChannelItemDeleted(id, nec) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. ch: params. channel-item-deleted. data: ${nec.length} items. params: ${upserted.length} items."
        )
      }
      val _nec = nec.asInstanceOf[List[Value]]
      val _paramS = upserted.filterNot { x =>
        val _id = paramR(x)
        _nec.exists(z => paramR(z) === _id)
      }
      val _jobS = pending.filterNot { x =>
        val _id = paramR(x)
        _nec.exists(z => paramR(z) === _id)
      }
      startProcessing(
        channel = channel,
        signals = signals,
        upserted = _paramS,
        pending = _jobS,
        error = error,
        publishedStatus = publishedStatus,
        running = running
      )
    // TODO |ZZZ|
    /*runDelete.run(_nec.map(paramR(_))) {
        case Left(cause) =>
          cause.foreach(thr =>
            log.error(s"$uniqueTag. ch: params. channel-delete-received. {}", thr)
          )
        case Right(_) =>
          if (enableTrace) {
            log.debug(s"$uniqueTag. ch: params. channel-delete-received. job complete.")
          }
      }*/

    case ChannelItemCreated(id, nec) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. ch: params. channel-item-created. data: ${nec.length} items. params: ${upserted.length} items."
        )
      }
      val _nec = nec.asInstanceOf[List[Value]]
      val _pending = _nec.filterNot { x =>
        val _id = paramR(x)
        upserted.exists(z => paramR(z) === _id && x === z)
      }
      if (_pending.nonEmpty) {
        startProcessing(
          channel = channel,
          signals = signals,
          upserted = upserted,
          pending = pending ++ _pending,
          error = error,
          publishedStatus = publishedStatus,
          running = running
        )
      }

    case ChannelItemUpdated(id, nec) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. ch: params. channel-item-updated. data: ${nec.length} items. params: ${upserted.length} items."
        )
      }
      val _nec = nec.asInstanceOf[List[Value]]
      val _pending = _nec.filterNot { x =>
        val _id = paramR(x)
        upserted.exists(z => paramR(z) === _id && x === z)
      }
      if (_pending.nonEmpty) {
        startProcessing(
          channel = channel,
          signals = signals,
          upserted = upserted,
          pending = pending ++ _pending,
          error = error,
          publishedStatus = publishedStatus,
          running = running
        )
      }

    case ChannelStatusChanged(id, _) if id === channel.channelId =>
    case msg =>
      log.error(
        s"$uniqueTag. basic-behavior. channel: ${channel.channelId}. tag: ${channel.tag}. unhandled $msg"
      )
      shutdown()
  }

  protected final def basicBehavior(
      channel: ChannelCreated,
      signals: List[SignalChannelCreated],
      upserted: List[Value],
      pending: List[Value],
      error: List[Value],
      publishedStatus: ChannelStatus,
      running: Boolean
  ): Unit =
    context.become(
      behaviors(
        channel = channel,
        signals = signals,
        upserted = upserted,
        pending = pending,
        error = error,
        publishedStatus = publishedStatus,
        running = running
      )
    )
}
