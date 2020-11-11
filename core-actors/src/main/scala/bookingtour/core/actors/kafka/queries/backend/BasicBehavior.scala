package bookingtour.core.actors.kafka.queries.backend

import java.time.Instant

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelStatusChanged
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.{
  SignalChannelCreated,
  SignalChannelStatusChanged
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.{
  SessionCreate,
  SessionDelete,
  SessionQuery
}
import bookingtour.protocols.core.actors.distributions.DistributionQueryEvent._
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelConsumerMessageReceived,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated,
  EdgeTopicUnreachableReceived
}
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.{EdgePublish, EdgePublishWithEnvelope}
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.messages.MessageEnvelope.EnvelopeChannel
import bookingtour.protocols.core.register.RegisterKey
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait BasicBehavior[R, Query, Answer] {
  _: Actor with ActorLogging with State[R, Query, Answer] =>

  private final def distributionBehaviors(
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated,
      deletedChannel: EdgeProducerChannelCreated,
      queryChannel: EdgeConsumerChannelCreated,
      statusChangedChannel: EdgeProducerChannelCreated,
      emptyAnswerChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeProducerChannelCreated,
      subscriptions: List[SessionCreated]
  ): Receive = {
    case EdgeChannelConsumerMessageReceived(id, envelope, data, _) if id === createChannel.id =>
      val tag = s"$uniqueTag. edge-consumer-message-received. session-create"
      val msg = data.asInstanceOf[SessionCreate]
      subscriptions.find(_.sessionId === msg.sessionId) match {
        case Some(s) =>
          log.error(s"$tag. receive session with non-unique id.")
          createdChannel.replayTo ! EdgePublish(
            createdChannel.id,
            channel = taggedChannel,
            msg = s,
            expiredAt = Instant.now().plusMillis(ttlMs)
          )

        case None =>
          val s = SessionCreated(
            sessionId = msg.sessionId,
            targetTag = msg.targetTag,
            consumerTopic = msg.consumerTopic
          )
          createdChannel.replayTo ! EdgePublish(
            createdChannel.id,
            channel = envelope.channel,
            msg = s,
            expiredAt = envelope.expiredAt
          )
          basicBehavior(
            signalChannel = signalChannel,
            createChannel = createChannel,
            createdChannel = createdChannel,
            deleteChannel = deleteChannel,
            deletedChannel = deletedChannel,
            queryChannel = queryChannel,
            statusChangedChannel = statusChangedChannel,
            emptyAnswerChannel = emptyAnswerChannel,
            answerChannel = answerChannel,
            subscriptions = subscriptions :+ s
          )
      }

    case EdgeChannelConsumerMessageReceived(id, envelope, data, _) if id === deleteChannel.id =>
      val tag = s"$uniqueTag. edge-consumer-message-received. session-delete"
      val msg = data.asInstanceOf[SessionDelete]
      subscriptions
        .find(_.sessionId === msg.sessionId) match {
        case None =>
          log.error(s"$tag. receive session with non-existed id.")

        case Some(_) =>
          deletedChannel.replayTo ! EdgePublish(
            id = deletedChannel.id,
            channel = envelope.channel,
            SessionDeleted(sessionId = msg.sessionId),
            expiredAt = envelope.expiredAt
          )
          basicBehavior(
            signalChannel = signalChannel,
            createChannel = createChannel,
            createdChannel = createdChannel,
            deleteChannel = deleteChannel,
            deletedChannel = deletedChannel,
            queryChannel = queryChannel,
            statusChangedChannel = statusChangedChannel,
            emptyAnswerChannel = emptyAnswerChannel,
            answerChannel = answerChannel,
            subscriptions = subscriptions.filterNot(_.sessionId === msg.sessionId)
          )
      }

    case EdgeChannelConsumerMessageReceived(id, envelope, data, _) if id === queryChannel.id =>
      val tag           = s"$uniqueTag. edge-consumer-message-received. query"
      val msg           = data.asInstanceOf[SessionQuery[Query]]
      val envelopeRoute = envelope.route.asInstanceOf[Bridge].revert()

      def env(key: RegisterKey): EnvelopeChannel =
        envelope.stamping.asInstanceOf[EnvelopeChannel].copy(route = envelopeRoute, bodyKey = key)

      subscriptions
        .find(_.sessionId === msg.sessionId) match {
        case None =>
          log.error(s"$tag. receive session with non-existed id.")
        /*emptyAnswerChannel.replayTo ! EdgePublishWithEnvelope(
            id = emptyAnswerChannel.id,
            channel = envelope.channel,
            envelope = env(sessionEmptyAnswerEntity.key),
            msg = SessionEmptyReceived(sessionId = msg.sessionId)
          )*/

        case Some(session) =>
          if (enableTrace) {
            log.info(s"$tag run.")
          }
          runQuery(msg.query) {
            case Left(cause) =>
              cause.foreach(err => log.error(s"$tag. $err."))
              emptyAnswerChannel.replayTo ! EdgePublishWithEnvelope(
                id = emptyAnswerChannel.id,
                channel = envelope.channel,
                envelope = env(sessionEmptyAnswerEntity.key),
                msg = SessionEmptyReceived(sessionId = session.sessionId)
              )

            case Right(xs) if xs.isEmpty =>
              if (enableTrace) {
                log.info(s"$tag. result empty.")
              }
              emptyAnswerChannel.replayTo ! EdgePublishWithEnvelope(
                id = emptyAnswerChannel.id,
                channel = envelope.channel,
                envelope = env(sessionEmptyAnswerEntity.key),
                msg = SessionEmptyReceived(sessionId = session.sessionId)
              )

            case Right(xs) =>
              if (enableTrace) {
                log.info(s"$tag. result ${xs.length}.")
              }
              answerChannel.replayTo ! EdgePublishWithEnvelope(
                id = answerChannel.id,
                channel = envelope.channel,
                envelope = env(sessionAnswerEntity.key),
                SessionAnswerReceived(
                  sessionId = session.sessionId,
                  data = xs
                )
              )
          }
      }

    case SignalChannelStatusChanged(_, status) =>
      if (enableTrace) {
        log.info(s"$uniqueTag. signal-channel-status-changed received. $status.")
      }
      for {
        s <- subscriptions
      } yield statusChangedChannel.replayTo ! EdgePublish(
        id = statusChangedChannel.id,
        channel = taggedChannel,
        msg = SessionStatusChangedReceived(sessionId = s.sessionId, status = status),
        expiredAt = Instant.now().plusMillis(ttlMs)
      )

    case ChannelStatusChanged(_, status) =>
      if (enableTrace) {
        log.info(s"$uniqueTag.channel-status-changed received. $status.")
      }
      for {
        s <- subscriptions
      } yield statusChangedChannel.replayTo ! EdgePublish(
        id = statusChangedChannel.id,
        channel = taggedChannel,
        msg = SessionStatusChangedReceived(sessionId = s.sessionId, status = status),
        expiredAt = Instant.now().plusMillis(ttlMs)
      )

    case EdgeTopicUnreachableReceived(_, topic, _) =>
      log.error(s"$uniqueTag. edge-topic-unreachable-received. topic: $topic")

    case msg =>
      log.error(s"$uniqueTag. receive undefined msg ${msg.getClass}")
      shutdown()
  }

  protected def basicBehavior(
      signalChannel: SignalChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated,
      deletedChannel: EdgeProducerChannelCreated,
      queryChannel: EdgeConsumerChannelCreated,
      statusChangedChannel: EdgeProducerChannelCreated,
      emptyAnswerChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeProducerChannelCreated,
      subscriptions: List[SessionCreated]
  ): Unit = {
    context.become(
      distributionBehaviors(
        signalChannel = signalChannel,
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        statusChangedChannel = statusChangedChannel,
        emptyAnswerChannel = emptyAnswerChannel,
        answerChannel = answerChannel,
        subscriptions = subscriptions
      )
    )
  }
}
