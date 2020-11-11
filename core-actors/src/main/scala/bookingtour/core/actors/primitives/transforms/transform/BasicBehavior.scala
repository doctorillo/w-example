package bookingtour.core.actors.primitives.transforms.transform

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand._
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import bookingtour.protocols.core.actors.operations.OpCommand.Completed
import cats.instances.uuid._
import cats.syntax.eq._

/**
  * © Alexey Toroshchin 2019.
  */
protected[transform] trait BasicBehavior[Input, Value, Id] {
  _: Actor with ActorLogging with State[Input, Value, Id] =>

  private final def onEvent(
      input: ChannelCreated,
      state: ChannelCreated,
      pending: List[() => Unit],
      running: Boolean
  ): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. on-event. pending: ${pending.length}. running: $running.")
    }
    if (!running && pending.nonEmpty) {
      for {
        h    <- pending.headOption
        tail = pending.tail
      } yield {
        basicBehavior(input = input, state = state, pending = tail, running = true)
        h()
      }
    } else {
      basicBehavior(input = input, state = state, pending = pending, running = running)
    }
  }

  private final def behaviors(
      input: ChannelCreated,
      state: ChannelCreated,
      pending: List[() => Unit],
      running: Boolean
  ): Receive = {
    case msg: ChannelCreate =>
      state.producer.forward(msg)

    case msg: SignalChannelCreate =>
      state.producer.forward(msg)

    case Completed =>
      log.info(s"$uniqueTag. completed. pending: ${pending.length}.")
      onEvent(input = input, state = state, pending = pending, running = false)

    case ChannelEmptySnapshotReceived(channelId) if channelId === input.channelId =>
      val _event = () => {
        if (enableTrace) {
          log.info(s"$uniqueTag. send channel-push-empty-snapshot.")
        }
        state.producer ! ChannelPushEmptySnapshot(state.channelId)
        self ! Completed
      }
      onEvent(input = input, state = state, pending = pending :+ _event, running = running)

    case ChannelSnapshotReceived(channelId, data) if channelId === input.channelId =>
      val _event = () => {
        if (enableTrace) {
          log.info(s"$uniqueTag. send channel-snapshot(${data.size}).")
        }
        onChange(data) {
          case Left(cause) =>
            cause.foreach(err => log.error(s"$uniqueTag. channel-snapshot-received. $err"))
            self ! Completed

          case Right(xs) if xs.isEmpty =>
            state.producer ! ChannelPushEmptySnapshot(state.channelId)
            self ! Completed

          case Right(xs) =>
            state.producer ! ChannelPushSnapshot(state.channelId, xs)
            self ! Completed
        }
      }
      onEvent(input = input, state = state, pending = pending :+ _event, running = running)

    case ChannelItemCreated(channelId, data) if channelId === input.channelId =>
      val _event = () => {
        if (enableTrace) {
          log.info(s"$uniqueTag. send channel-item-created(${data.size}).")
        }
        onChange(data) {
          case Left(cause) =>
            cause.foreach(err => log.error(s"$uniqueTag. channel-item-created. $err"))
            self ! Completed

          case Right(xs) if xs.isEmpty =>
            self ! Completed

          case Right(xs) =>
            state.producer ! ChannelPushCreate(state.channelId, xs)
            self ! Completed
        }
      }
      onEvent(input = input, state = state, pending = pending :+ _event, running = running)

    case ChannelItemUpdated(channelId, data) if channelId === input.channelId =>
      val _event = () => {
        if (enableTrace) {
          log.info(s"$uniqueTag. send channel-item-updated(${data.size}).")
        }
        onChange(data) {
          case Left(cause) =>
            cause.foreach(err => log.error(s"$uniqueTag. channel-item-updated. $err"))
            self ! Completed

          case Right(xs) if xs.isEmpty =>
            self ! Completed

          case Right(xs) =>
            state.producer ! ChannelPushUpdate(state.channelId, xs)
            self ! Completed
        }
      }
      onEvent(input = input, state = state, pending = pending :+ _event, running = running)

    case ChannelItemDeleted(channelId, data) if channelId === input.channelId =>
      val _event = () => {
        if (enableTrace) {
          log.info(s"$uniqueTag. send channel-item-deleted(${data.size}).")
        }
        onChange(data) {
          case Left(cause) =>
            cause.foreach(err => log.error(s"$uniqueTag. channel-item-deleted. $err"))
            self ! Completed

          case Right(xs) if xs.isEmpty =>
            self ! Completed

          case Right(xs) =>
            state.producer ! ChannelPushDelete(state.channelId, xs)
            self ! Completed
        }
      }
      onEvent(input = input, state = state, pending = pending :+ _event, running = running)

    case ChannelStatusChanged(channelId, s) if channelId === input.channelId =>
      val _event = () => {
        if (enableTrace) {
          log.info(s"$uniqueTag. send channel-status-changed($s).")
        }
        state.producer ! ChannelPushStatus(state.channelId, s)
        self ! Completed
      }
      onEvent(input = input, state = state, pending = pending :+ _event, running = running)

    case msg =>
      log.error(s"$uniqueTag. basic-behavior. unhandled $msg")
      shutdown()
  }

  protected final def basicBehavior(
      input: ChannelCreated,
      state: ChannelCreated,
      pending: List[() => Unit],
      running: Boolean
  ): Unit = context.become(
    behaviors(
      input = input,
      state = state,
      pending = pending,
      running = running
    )
  )
}
