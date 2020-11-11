package bookingtour.core.actors.primitives.channel.basic

import scala.collection.immutable.Map

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.actors.aggregators._
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand._
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent._
import bookingtour.protocols.core.actors.internal.PendingItem
import bookingtour.protocols.core.actors.internal.PendingItem.{PendingDelete, PendingSnapshot, PendingUpsert}
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.instances.uuid._
import cats.syntax.option._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[basic] trait BasicBehavior[Value, Id, PartitionKey] {
  _: Actor
    with ActorLogging with State[Value, Id, PartitionKey] with SignalBehavior[Value, Id, PartitionKey]
    with ConsumerBehavior[Value, Id, PartitionKey] with QueryBehavior[Value, Id, PartitionKey] =>

  private final val internalTrace = false

  protected final def basicBehavior(
      sequenceId: SequenceNr,
      consumers: List[ChannelCreated],
      pendingConsumers: List[ChannelCreated],
      signals: List[SignalChannelCreated],
      state: Map[PartitionKey, List[Value]],
      publishedStatus: ChannelStatus,
      pendingStatus: Option[(SequenceNr, ChannelStatus)],
      pending: List[PendingItem],
      running: Boolean
  ): Unit =
    context.become(
      signalBehaviors(
        sequenceId = sequenceId,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = publishedStatus,
        pendingStatus = pendingStatus,
        pending = pending,
        running = running
      ).orElse(
          consumerBehaviors(
            sequenceId = sequenceId,
            consumers = consumers,
            pendingConsumers = pendingConsumers,
            signals = signals,
            state = state,
            publishedStatus = publishedStatus,
            pendingStatus = pendingStatus,
            pending = pending,
            running = running
          )
        )
        .orElse(
          queryBehaviors(
            state = state,
            publishedStatus = publishedStatus,
            running = running
          )
        )
        .orElse(
          behaviors(
            sequenceId = sequenceId,
            consumers = consumers,
            pendingConsumers = pendingConsumers,
            signals = signals,
            state = state,
            publishedStatus = publishedStatus,
            pendingStatus = pendingStatus,
            pending = pending,
            running = running
          )
        )
        .orElse(defaultBehavior(consumers, signals))
    )

  protected final def startProcessing(
      sequenceId: SequenceNr,
      consumers: List[ChannelCreated],
      pendingConsumers: List[ChannelCreated],
      signals: List[SignalChannelCreated],
      state: Map[PartitionKey, List[Value]],
      publishedStatus: ChannelStatus,
      pendingStatus: Option[(SequenceNr, ChannelStatus)],
      pending: List[PendingItem],
      running: Boolean
  ): Unit = {
    if (enableTrace) {
      log.info(
        s"$uniqueTag. start-processing. running: $running. state: ${state.nonEmpty}. status: $publishedStatus. pending-status: $pendingStatus. consumers: ${consumers.length}. signals: ${signals.length}. pending: ${pending.length}."
      )
    }
    if (!running && pending.nonEmpty) {
      for {
        h <- pending.headOption
        t = pending.tail
      } yield {
        h match {
          case pending: PendingSnapshot[PartitionKey, Value] =>
            basicBehavior(
              sequenceId = sequenceId,
              consumers = consumers,
              pendingConsumers = pendingConsumers,
              signals = signals,
              state = state,
              publishedStatus = publishedStatus,
              pendingStatus = pendingStatus,
              pending = t,
              running = true
            )
            pending.run(state)

          case pending: PendingUpsert[PartitionKey, Value] =>
            basicBehavior(
              sequenceId = sequenceId,
              consumers = consumers,
              pendingConsumers = pendingConsumers,
              signals = signals,
              state = state,
              publishedStatus = publishedStatus,
              pendingStatus = pendingStatus,
              pending = t,
              running = true
            )
            pending.run(state)

          case pending: PendingDelete[PartitionKey, Value] =>
            basicBehavior(
              sequenceId = sequenceId,
              consumers = consumers,
              pendingConsumers = pendingConsumers,
              signals = signals,
              state = state,
              publishedStatus = publishedStatus,
              pendingStatus = pendingStatus,
              pending = t,
              running = true
            )
            pending.run(state)
        }
      }
    } else if (!running && pendingStatus.nonEmpty) {
      for {
        s <- pendingStatus.map(_._2)
      } {
        val pStatus = if (s =!= ChannelStatus.Undefined && state.isEmpty) {
          sendStatus(uniqueTag, log, enableTrace, consumers, signals, ChannelStatus.Undefined)
          ChannelStatus.Undefined
        } else if (s =!= publishedStatus) {
          sendStatus(uniqueTag, log, enableTrace, consumers, signals, s)
          s
        } else {
          publishedStatus
        }
        basicBehavior(
          sequenceId = sequenceId,
          consumers = consumers,
          pendingConsumers = pendingConsumers,
          signals = signals,
          state = state,
          publishedStatus = pStatus,
          pendingStatus = None,
          pending = pending,
          running = running
        )
      }
    } else {
      basicBehavior(
        sequenceId = sequenceId,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = publishedStatus,
        pendingStatus = pendingStatus,
        pending = pending,
        running = running
      )
    }
  }

  private final def behaviors(
      sequenceId: SequenceNr,
      consumers: List[ChannelCreated],
      pendingConsumers: List[ChannelCreated],
      signals: List[SignalChannelCreated],
      state: Map[PartitionKey, List[Value]],
      publishedStatus: ChannelStatus,
      pendingStatus: Option[(SequenceNr, ChannelStatus)],
      pending: List[PendingItem],
      running: Boolean
  ): Receive = {
    case msg: AggregatePartitionResult[PartitionKey, Value] =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. aggregate-partition-result received. published-status: $publishedStatus. created: ${msg.created.length} deleted: ${msg.deleted.length} updated: ${msg.updated.length}. state: ${msg.nonEmpty}. consumers: ${consumers.length}. pending-consumers: ${pendingConsumers.length}. signals: ${signals.length}. pending: ${pending.length}. pending-status: $pendingStatus."
        )
      }
      pendingConsumers.foreach { x => sendToChannel(uniqueTag, log, enableTrace, x, msg.state.toList.flatMap(_._2)) }
      sendToConsumer(
        uniqueTag = uniqueTag,
        log = log,
        enableTrace = internalTrace,
        consumers = consumers,
        value = msg
      )
      val pStatus = if (msg.state.isEmpty) {
        sendStatus(uniqueTag, log, enableTrace, consumers, signals, ChannelStatus.Undefined)
        ChannelStatus.Undefined
      } else {
        publishedStatus
      }
      startProcessing(
        sequenceId = sequenceId,
        consumers = consumers ++ pendingConsumers,
        pendingConsumers = List.empty,
        signals = signals,
        state = msg.state,
        publishedStatus = pStatus,
        pendingStatus = pendingStatus,
        pending = pending,
        running = false
      )

    case ChannelPushEmptySnapshot(channelId) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-empty-snapshot received. pending: ${pending.length}. status-published: $publishedStatus."
        )
      }
      val pid = sequenceId.next
      val evt = PendingSnapshot(
        pid,
        onSnapshotEvent(
          s"$uniqueTag. channel-push-empty-snapshot. pending-id: $pid",
          pid,
          List.empty
        )
      )
      val pendingItem = List(evt)
      if (publishedStatus =!= ChannelStatus.Busy) {
        sendStatus(
          uniqueTag = uniqueTag,
          log = log,
          enableTrace = internalTrace,
          consumers = consumers ++ pendingConsumers,
          signals = signals,
          status = ChannelStatus.Busy
        )
      }
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = ChannelStatus.Busy,
        pendingStatus = pendingStatus,
        pending = pendingItem,
        running = running
      )

    case ChannelPushSnapshot(channelId, data) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-snapshot received. pending: ${pending.length}. status-published: $publishedStatus. data: ${data.length}."
        )
      }
      val nec = data.asInstanceOf[List[Value]]
      val pid = sequenceId.next
      val evt = PendingSnapshot(
        id = pid,
        onSnapshotEvent(
          s"$uniqueTag. channel-push-snapshot. pending-id: $pid",
          pid,
          nec
        )
      )
      if (publishedStatus =!= ChannelStatus.Busy) {
        sendStatus(
          uniqueTag = uniqueTag,
          log = log,
          enableTrace = internalTrace,
          consumers = consumers ++ pendingConsumers,
          signals = signals,
          status = ChannelStatus.Busy
        )
      }
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = ChannelStatus.Busy,
        pendingStatus = pendingStatus,
        pending = List(evt),
        running = running
      )

    case ChannelPushCreate(channelId, data) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-create received. pending: ${pending.length}. status-published: $publishedStatus. data: ${data.length}."
        )
      }
      val nec = data.asInstanceOf[List[Value]]
      val pid = sequenceId.next
      val evt = PendingUpsert(
        pid,
        onCreateEvent(s"$uniqueTag. channel-push-create. pending-id: $pid", pid, nec)
      )
      val pendingItem = pending :+ evt
      if (publishedStatus =!= ChannelStatus.Busy) {
        sendStatus(
          uniqueTag = uniqueTag,
          log = log,
          enableTrace = internalTrace,
          consumers = consumers ++ pendingConsumers,
          signals = signals,
          status = ChannelStatus.Busy
        )
      }
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = ChannelStatus.Busy,
        pendingStatus = pendingStatus,
        pending = pendingItem,
        running = running
      )

    case ChannelPushUpdate(channelId, data) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-update received. pending: ${pending.length}. status-published: $publishedStatus. data: ${data.length}."
        )
      }
      val nec = data.asInstanceOf[List[Value]]
      val pid = sequenceId.next
      val evt = PendingUpsert(
        pid,
        onUpdateEvent(s"$uniqueTag. channel-push-update. pending-id: $pid", pid, nec)
      )
      val pendingItem = pending :+ evt
      if (publishedStatus =!= ChannelStatus.Busy) {
        sendStatus(
          uniqueTag = uniqueTag,
          log = log,
          enableTrace = internalTrace,
          consumers = consumers ++ pendingConsumers,
          signals = signals,
          status = ChannelStatus.Busy
        )
      }
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = ChannelStatus.Busy,
        pendingStatus = pendingStatus,
        pending = pendingItem,
        running = running
      )

    case ChannelPushDelete(channelId, data) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-delete received. pending: ${pending.length}. status-published: $publishedStatus. data: ${data.length}."
        )
      }
      val nec = data.asInstanceOf[List[Value]]
      val pid = sequenceId.next
      val evt = PendingDelete(
        pid,
        onDeleteEvent(s"$uniqueTag. channel-push-delete. pending-id: $pid", pid, nec)
      )
      val pendingItem = pending :+ evt
      if (publishedStatus =!= ChannelStatus.Busy) {
        sendStatus(
          uniqueTag = uniqueTag,
          log = log,
          enableTrace = internalTrace,
          consumers = consumers ++ pendingConsumers,
          signals = signals,
          status = ChannelStatus.Busy
        )
      }
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = ChannelStatus.Busy,
        pendingStatus = pendingStatus,
        pending = pendingItem,
        running = running
      )

    case ChannelPushDeleteId(channelId, data) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-delete-id received. pending: ${pending.length}. status-published: $publishedStatus. data: ${data.length}."
        )
      }
      val nec = data.asInstanceOf[List[Id]]
      val pid = sequenceId.next
      val evt = PendingDelete(
        pid,
        onDeleteIdEvent(
          s"$uniqueTag. channel-push-delete-id. pending-id: $pid",
          pid,
          nec
        )
      )
      val pendingItem = pending :+ evt
      if (publishedStatus =!= ChannelStatus.Busy) {
        sendStatus(
          uniqueTag = uniqueTag,
          log = log,
          enableTrace = internalTrace,
          consumers = consumers ++ pendingConsumers,
          signals = signals,
          status = ChannelStatus.Busy
        )
      }
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = ChannelStatus.Busy,
        pendingStatus = pendingStatus,
        pending = pendingItem,
        running = running
      )

    case ChannelPushStatus(channelId, _status) if channelId === managerChannelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-push-status received. pending: ${pending.length}. status-published: $publishedStatus. status-received: ${_status}."
        )
      }
      val pid = sequenceId.next
      startProcessing(
        sequenceId = pid,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals,
        state = state,
        publishedStatus = publishedStatus,
        pendingStatus = (pid, _status).some,
        pending = pending,
        running = running
      )
  }
}
