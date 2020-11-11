package bookingtour.core.actors.kafka.enricher

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.{
  SignalChannelCreated,
  SignalChannelStatusChanged
}
import bookingtour.protocols.core.actors.internal.PendingId
import bookingtour.protocols.core.actors.internal.PendingId.{PendingDeleteId, PendingTruncateId, PendingUpsertId}
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeConsumerChannelCreated, EdgeConsumerMessageReceived}
import bookingtour.protocols.core.actors.operations.OpCommand.{Completed, Start}
import bookingtour.protocols.core.db.DbAnswer
import bookingtour.protocols.core.db.DbEventPayload.BaseEntity
import bookingtour.protocols.core.db.enumeration.DbEvent
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.instances.uuid._
import cats.syntax.option._
import cats.syntax.order._
import cats.syntax.semigroup._

/**
  * © Alexey Toroshchin 2019.
  */
protected[enricher] trait BasicBehavior[Value, DbId, Id, Stamp] {
  _: Actor with ActorLogging with State[Value, DbId, Id, Stamp] =>

  private final def runUpdate(channel: ActorProducer[Value, Id], id: List[Id]): Unit = {
    zioRuntime.unsafeRunAsync(readUpdated.use(_.ask(id.map(fromId)))) {
      case zio.Exit.Failure(cause) =>
        cause.failures.foreach(trh => log.error(s"$uniqueTag. run-updated. {}", trh))
        self ! Completed

      case zio.Exit.Success(payload) =>
        payload match {
          case DbAnswer.Empty =>
            if (enableTrace) {
              log.info(
                s"$uniqueTag. run-updated. receive empty answer. id: ${id.length}."
              )
            }

          case p: DbAnswer.NonEmpty[Value] =>
            if (enableTrace) {
              log.info(
                s"$uniqueTag. run-updated. receive non empty (${p.xs.length}) answer."
              )
            }
            channel.x ! ChannelPushUpdate(channelStateId, p.xs)
        }
        self ! Completed
    }
  }

  protected final def reload(
      sequenceId: SequenceNr,
      truncateCh: EdgeConsumerChannelCreated,
      updateCh: EdgeConsumerChannelCreated,
      channel: ActorProducer[Value, Id],
      signals: List[(SignalChannelCreated, ChannelStatus)],
      pending: List[PendingId[Id]],
      running: Boolean,
      lastStatus: ChannelStatus,
      breakerStatus: ChannelStatus
  ): Unit =
    context.become(
      basicBehavior(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        pending = pending,
        signals = signals,
        running = running,
        lastStatus = lastStatus,
        breakerStatus = breakerStatus
      )
    )

  private final def startProcessing(
      sequenceId: SequenceNr,
      truncateCh: EdgeConsumerChannelCreated,
      updateCh: EdgeConsumerChannelCreated,
      channel: ActorProducer[Value, Id],
      signals: List[(SignalChannelCreated, ChannelStatus)],
      pending: List[PendingId[Id]],
      running: Boolean,
      lastStatus: ChannelStatus,
      breakerStatus: ChannelStatus
  ): Unit = {
    val tag = s"$uniqueTag. start-processing"
    if (enableTrace) {
      log.info(
        s"$tag. last-status: $lastStatus. breaker-status: $breakerStatus. running: $running. pending: ${pending.length}. timer-active: ${timerActive()}."
      )
    }
    if (!running && pending.nonEmpty && !timerActive()) {
      for {
        h <- pending.headOption
        t = pending.tail
      } {
        val (cmd, tail) = h match {
          case PendingTruncateId(_) =>
            val cmd = () => {
              channel.x ! ChannelPushEmptySnapshot(channelStateId)
              self ! Completed
            }
            (cmd, t)

          case msg @ PendingDeleteId(_, data) =>
            val (job, jobTail) = data.splitAt(batchSize)
            val cmd = () => {
              channel.x ! ChannelPushDeleteId(
                channelStateId,
                job
              )
              self ! Completed
            }
            if (jobTail.nonEmpty) {
              (
                cmd,
                List(msg.copy(data = jobTail)) ++ t
              )
            } else {
              (cmd, t)
            }

          case msg @ PendingUpsertId(_, data) =>
            val (job, jobTail) = data.splitAt(batchSize)
            val cmd            = () => runUpdate(channel, job)
            if (jobTail.nonEmpty) {
              (
                cmd,
                List(msg.copy(data = jobTail)) ++ t
              )
            } else {
              (cmd, t)
            }
        }
        if (enableTrace) {
          log.info(s"$tag. ready for run command.")
        }
        if (lastStatus =!= ChannelStatus.Busy) {
          channel.x ! ChannelPushStatus(channelStateId, ChannelStatus.Busy)
        }
        reload(
          sequenceId = sequenceId,
          truncateCh = truncateCh,
          updateCh = updateCh,
          channel = channel,
          signals = signals,
          pending = tail,
          running = true,
          lastStatus = ChannelStatus.Busy,
          breakerStatus = breakerStatus
        )
        cmd()
      }
    } else if (!running && pending.isEmpty && !timerActive() && lastStatus === ChannelStatus.Busy) {
      val calculatedStatus = ChannelStatus.ready |+| breakerStatus
      channel.x ! ChannelPushStatus(channelStateId, calculatedStatus)
      if (enableTrace) {
        log.info(s"$tag. channel-push-status. $calculatedStatus.")
      }
      reload(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        pending = pending,
        signals = signals,
        running = running,
        lastStatus = calculatedStatus,
        breakerStatus = breakerStatus
      )
    } else {
      reload(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        pending = pending,
        signals = signals,
        running = running,
        lastStatus = lastStatus,
        breakerStatus = breakerStatus
      )
    }
  }

  protected final def basicBehavior(
      sequenceId: SequenceNr,
      truncateCh: EdgeConsumerChannelCreated,
      updateCh: EdgeConsumerChannelCreated,
      channel: ActorProducer[Value, Id],
      signals: List[(SignalChannelCreated, ChannelStatus)],
      pending: List[PendingId[Id]],
      running: Boolean,
      lastStatus: ChannelStatus,
      breakerStatus: ChannelStatus
  ): Receive = {
    case msg: ChannelCreate =>
      channel.x.forward(msg)

    case msg: ChannelDelete =>
      channel.x.forward(msg)

    case msg: SignalChannelCreate =>
      channel.x.forward(msg)

    case msg: SignalChannelDelete =>
      channel.x.forward(msg)

    case msg: SignalChannelCreated =>
      if (enableTrace) {
        log.info(s"$uniqueTag. signal-channel-created received. tag: ${msg.tag}.")
      }
      reload(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        signals = signals :+ ((msg, ChannelStatus.Undefined)),
        pending = pending,
        running = running,
        lastStatus = lastStatus,
        breakerStatus = breakerStatus
      )

    case SignalChannelStatusChanged(id, s) =>
      val sxs = signals.find(_._1.channelId === id).toList.flatMap { x =>
        if (enableTrace) {
          log.info(
            s"$uniqueTag. signal-channel-status-changed received. status: $lastStatus. received-status: $s."
          )
        }
        signals.filterNot(_._1.channelId === x._1.channelId) :+ ((x._1, s))
      }
      val pendingStatus =
        sxs.filterNot(_._2 === ChannelStatus.Undefined).foldLeft(ChannelStatus.ready) { (acc, x) => acc |+| x._2 }
      startProcessing(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        signals = sxs,
        pending = pending,
        running = running,
        lastStatus = lastStatus,
        breakerStatus = pendingStatus
      )

    case Start =>
      if (!running) {
        startProcessing(
          sequenceId = sequenceId,
          truncateCh = truncateCh,
          updateCh = updateCh,
          channel = channel,
          signals = signals,
          pending = pending,
          running = running,
          lastStatus = lastStatus,
          breakerStatus = breakerStatus
        )
      }

    case Completed =>
      if (enableTrace) {
        log.info(s"$uniqueTag. completed.")
      }
      startProcessing(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        signals = signals,
        pending = pending,
        running = false,
        lastStatus = lastStatus,
        breakerStatus = breakerStatus
      )

    case EdgeConsumerMessageReceived(id, _, _, _) if id === truncateCh.id =>
      val pid         = sequenceId.next
      val pendingItem = List(PendingTruncateId[Id](pid))
      reload(
        sequenceId = sequenceId,
        truncateCh = truncateCh,
        updateCh = updateCh,
        channel = channel,
        pending = pendingItem,
        signals = signals,
        running = running,
        lastStatus = lastStatus,
        breakerStatus = breakerStatus
      )
      onPending(pendingItem)

    case EdgeConsumerMessageReceived(id, _, msg, _) if id === updateCh.id =>
      val tag = s"$uniqueTag. channel update"
      val pid = sequenceId.next
      try {
        val action = msg.asInstanceOf[BaseEntity[Id, Stamp]] match {
          case BaseEntity(_, _, id, DbEvent.Insert, _) =>
            PendingUpsertId[Id](pid, List(id)).some

          case BaseEntity(_, _, id, DbEvent.Update, _) =>
            PendingUpsertId[Id](pid, List(id)).some

          case BaseEntity(_, _, id, DbEvent.Delete, _) =>
            PendingDeleteId[Id](pid, List(id)).some

          case _ =>
            none
        }
        action match {
          case None =>
          case Some(_: PendingTruncateId[Id]) =>
            log.error(s"$tag. truncate received.")

          case Some(upsert: PendingUpsertId[Id]) =>
            pending.lastOption match {
              case None =>
                /*if (enableTrace) {
                  log.info(s"$tag. upsert received. pending empty.")
                }*/
                val pendingItem = List(
                  PendingUpsertId(
                    sequenceId = pid,
                    data = upsert.data
                  )
                )
                reload(
                  sequenceId = sequenceId,
                  truncateCh = truncateCh,
                  updateCh = updateCh,
                  channel = channel,
                  pending = pendingItem,
                  signals = signals,
                  running = running,
                  lastStatus = lastStatus,
                  breakerStatus = breakerStatus
                )
                onPending(pendingItem)

              case Some(PendingUpsertId(_, data)) =>
                /*if (enableTrace) {
                  log.info(
                    s"$tag. upsert received. pending items: ${pending.length}. last pending item: PendingUpsertId."
                  )
                }*/
                val last = PendingUpsertId(
                  sequenceId = pid,
                  data = data ++ upsert.data
                )
                val pendingItem = pending.init :+ last
                reload(
                  sequenceId = sequenceId,
                  truncateCh = truncateCh,
                  updateCh = updateCh,
                  channel = channel,
                  signals = signals,
                  pending = pendingItem,
                  running = running,
                  lastStatus = lastStatus,
                  breakerStatus = breakerStatus
                )
                onPending(pendingItem)

              case Some(_) =>
                /*if (enableTrace) {
                  log.info(s"$tag. upsert received. pending items: ${pending.length}.")
                }*/
                val pendingItem = pending :+ upsert
                reload(
                  sequenceId = sequenceId,
                  truncateCh = truncateCh,
                  updateCh = updateCh,
                  channel = channel,
                  signals = signals,
                  pending = pendingItem,
                  running = running,
                  lastStatus = lastStatus,
                  breakerStatus = breakerStatus
                )
                onPending(pendingItem)
            }

          case Some(delete: PendingDeleteId[Id]) =>
            pending.lastOption match {
              case None =>
                reload(
                  sequenceId = sequenceId,
                  truncateCh = truncateCh,
                  updateCh = updateCh,
                  channel = channel,
                  signals = signals,
                  pending = List(delete),
                  running = running,
                  lastStatus = lastStatus,
                  breakerStatus = breakerStatus
                )
                onPending(List(delete))

              case Some(PendingDeleteId(_, data)) =>
                val pendingItem = pending.init :+ delete.copy(data = data ++ delete.data)
                reload(
                  sequenceId = sequenceId,
                  truncateCh = truncateCh,
                  updateCh = updateCh,
                  channel = channel,
                  signals = signals,
                  pending = pendingItem,
                  running = running,
                  lastStatus = lastStatus,
                  breakerStatus = breakerStatus
                )
                onPending(pendingItem)

              case Some(_) =>
                val pendingItem = pending :+ delete
                reload(
                  sequenceId = sequenceId,
                  truncateCh = truncateCh,
                  updateCh = updateCh,
                  channel = channel,
                  signals = signals,
                  pending = pendingItem,
                  running = running,
                  lastStatus = lastStatus,
                  breakerStatus = breakerStatus
                )
                onPending(pendingItem)
            }
        }
      } catch {
        case thr: Throwable =>
          log.error(s"$uniqueTag. edge-consumer-message-received. ${thr.getMessage}.")
      }

    case _: EdgeConsumerMessageReceived[_] =>
    case msg =>
      val tag = s"$uniqueTag. receive unhandled"
      log.error(s"$tag. channel-state-id: $channelStateId.")
      log.error(s"$tag. channel-update-type: ${updateEntity.key}.")
      log.error(s"$tag. channel-update-id: ${updateCh.id}.")
      log.error(s"$tag. channel-truncate-type: ${truncateEntity.key}.")
      log.error(s"$tag. channel-truncate-id: ${truncateCh.id}.")
      log.error(s"$tag. message: $msg.")
      shutdown()
  }
}
