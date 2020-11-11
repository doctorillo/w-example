package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging, Stash, Timers}
import bookingtour.core.actors.kafka.queries.client.query.QueryCachedClient.QueryCacheConfig
import bookingtour.protocols.core.actors.distributions.DistributionQueryCommand.SessionQuery
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelConsumerMessageReceived,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated,
  EdgeTopicUnreachableReceived
}
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.EdgePublishWithEnvelope
import bookingtour.protocols.core.actors.operations.OpCommand.Start
import bookingtour.protocols.core.messages.MessageEnvelope.EnvelopeChannel
import bookingtour.protocols.core.messages.{DropCache, PostStamp, RunnableQuery}
import bookingtour.protocols.core.values.api.QueryResult
import cats.data.NonEmptyList
import cats.instances.uuid._
import cats.syntax.order._
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
protected[query] trait BasicBehavior[Query, Value] {
  _: Actor with ActorLogging with Timers with Stash with State[Query, Value] =>

  private final val tag: String = s"$uniqueTag. basic-behavior."

  private final def behaviors(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeConsumerChannelCreated,
      emptyAnswerChannel: EdgeConsumerChannelCreated,
      statusChangedChannel: EdgeConsumerChannelCreated,
      queries: List[QueueCell[Query]]
  ): Receive = {
    case Start =>
      if (queries.nonEmpty) {
        val now               = Instant.now()
        val (active, expired) = queries.partition(_.expiredAt.isAfter(now))
        expired.foreach { x =>
          log.error(s"$tag clean queries. send time-out.")
          x.consumer ! makeErrorAnswer(List("time-out."))
        }
        basicBehavior(
          createChannel = createChannel,
          createdChannel = createdChannel,
          deleteChannel = deleteChannel,
          deletedChannel = deletedChannel,
          queryChannel = queryChannel,
          answerChannel = answerChannel,
          emptyAnswerChannel = emptyAnswerChannel,
          statusChangedChannel = statusChangedChannel,
          queries = active
        )
      }

    case AddToQueue(cell) =>
      if (enableTrace) {
        log.info(s"$tag add to queue.")
      }
      basicBehavior(
        createChannel = createChannel,
        createdChannel = createdChannel,
        deleteChannel = deleteChannel,
        deletedChannel = deletedChannel,
        queryChannel = queryChannel,
        answerChannel = answerChannel,
        emptyAnswerChannel = emptyAnswerChannel,
        statusChangedChannel = statusChangedChannel,
        queries = queries :+ cell.asInstanceOf[QueueCell[Query]]
      )

    case DropCache =>
      cacheConfig.foreach(config =>
        zioRuntime.unsafeRunAsync(config.alg.deleteBucket())({
          case zio.Exit.Failure(cause) =>
            cause.failures.foreach(log.error(s"$tag. {}", _))

          case zio.Exit.Success(_) =>
        })
      )

    case RunnableQuery(query, expiredAt, replayTo) =>
      if (enableTrace) {
        log.info(s"$tag receive query.")
      }
      val _query = query.asInstanceOf[Query]
      def runQ() = {
        if (enableTrace) {
          log.info(s"$tag start-query.")
        }
        val envelope = EnvelopeChannel(
          id = UUID.randomUUID(),
          route = route,
          channel = taggedChannel,
          bodyKey = sessionQueryEntity.key,
          stamps = PostStamp.one(postOffice),
          expiredAt = expiredAt
        )
        queryChannel.replayTo ! EdgePublishWithEnvelope(
          id = queryChannel.id,
          channel = envelope.channel,
          envelope = envelope,
          msg = SessionQuery(sessionId = sessionId, query = query)
        )
        self ! AddToQueue(
          QueueCell(
            envelopeId = envelope.id,
            query = _query,
            expiredAt = envelope.expiredAt,
            consumer = replayTo
          )
        )
      }

      cacheConfig match {
        case None =>
          runQ()

        case Some(QueryCacheConfig(alg, _)) =>
          zioRuntime.unsafeRunAsync(alg.get(_query)) {
            case zio.Exit.Failure(cause) =>
              cause.failures.foreach(log.error(s"$tag. {}", _))
              runQ()

            case zio.Exit.Success(xs) if xs.isEmpty =>
              runQ()

            case zio.Exit.Success(xs) =>
              replayTo ! QueryResult.fromList(xs)
          }
      }

    case EdgeChannelConsumerMessageReceived(id, _, _, _) if createdChannel.id === id =>
      if (enableTrace) {
        log.info(s"$uniqueTag. ch: created. receive.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, _, _) if deletedChannel.id === id =>
      if (enableTrace) {
        log.info(s"$uniqueTag. ch: deleted. receive.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, _, _) if statusChangedChannel.id === id =>
      if (enableTrace) {
        log.info(s"$uniqueTag. ch: state-changed. receive.")
      }
      cacheConfig.foreach(config =>
        zioRuntime.unsafeRunAsync(config.alg.deleteBucket())({
          case zio.Exit.Failure(cause) =>
            cause.failures.foreach(log.error(s"$tag. {}", _))

          case zio.Exit.Success(_) =>
        })
      )

    case EdgeChannelConsumerMessageReceived(id, env, _, _) if emptyAnswerChannel.id === id =>
      if (enableTrace) {
        log.info(s"$uniqueTag. ch: empty answer. receive.")
      }
      queries.find(_.envelopeId === env.id) match {
        case None =>
          log.error(s"$tag received answer expired.")

        case Some(QueueCell(_, query, _, consumer)) =>
          val _query = query.asInstanceOf[Query]
          val errors = env.stamps.toList.flatMap(_.errors.toList)
          consumer ! makeErrorAnswer(errors)
          cacheConfig.foreach(config =>
            zioRuntime.unsafeRunAsync(config.alg.delete(_query))({
              case zio.Exit.Failure(cause) =>
                cause.failures.foreach(log.error(s"$tag. {}", _))

              case zio.Exit.Success(_) =>
            })
          )
          basicBehavior(
            createChannel = createChannel,
            createdChannel = createdChannel,
            deleteChannel = deleteChannel,
            deletedChannel = deletedChannel,
            queryChannel = queryChannel,
            answerChannel = answerChannel,
            emptyAnswerChannel = emptyAnswerChannel,
            statusChangedChannel = statusChangedChannel,
            queries = queries.filterNot(_.envelopeId === env.id)
          )
      }

    case EdgeChannelConsumerMessageReceived(id, env, data, _) if answerChannel.id === id =>
      val effect = for {
        cell <- queries
                 .find(_.envelopeId === env.id)
                 .fold[ZIO[Any, String, QueueCell[Query]]](ZIO.fail("envelope not found"))(cell => ZIO.succeed(cell))
        msg <- sessionAnswerEntity.cast(data)
        answer <- ZIO
                   .effect(QueryResult.fromList(msg.data))
                   .catchAll(trh => ZIO.fail(s"from-nec. ${trh.getMessage}."))
        _ <- ZIO
              .effect(cell.consumer ! answer)
              .catchAll(thr => ZIO.fail(s"$uniqueTag. return answer. ${thr.getMessage}."))
        _ <- if (cacheConfig.isDefined && msg.data.nonEmpty) {
              cacheConfig.get.alg
                .put(cell.query, NonEmptyList.fromListUnsafe(msg.data))
            } else {
              ZIO.unit
            }
      } yield cell
      zioRuntime.unsafeRunAsync(effect) {
        case zio.Exit.Failure(cause) =>
          cause.failures.foreach(err => log.error(s"$tag. $err"))

        case zio.Exit.Success(_) =>
          basicBehavior(
            createChannel = createChannel,
            createdChannel = createdChannel,
            deleteChannel = deleteChannel,
            deletedChannel = deletedChannel,
            queryChannel = queryChannel,
            answerChannel = answerChannel,
            emptyAnswerChannel = emptyAnswerChannel,
            statusChangedChannel = statusChangedChannel,
            queries = queries.filterNot(_.envelopeId === env.id)
          )
      }

    case EdgeChannelConsumerMessageReceived(_, env, _, _) =>
      log.error(
        s"$tag consumer-message-received. receive undefined message. ${env.bodyKey.typeTag}."
      )
      shutdown()

    case _: EdgeTopicUnreachableReceived =>
      log.error(s"$tag edge-topic-unreachable-received.")

    case msg =>
      log.error(s"$tag receive undefined message. ${msg.getClass.getName}")
      shutdown()
  }

  protected def basicBehavior(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      queryChannel: EdgeProducerChannelCreated,
      answerChannel: EdgeConsumerChannelCreated,
      emptyAnswerChannel: EdgeConsumerChannelCreated,
      statusChangedChannel: EdgeConsumerChannelCreated,
      queries: List[QueueCell[Query]]
  ): Unit = context.become(
    behaviors(
      createChannel = createChannel,
      createdChannel = createdChannel,
      deleteChannel = deleteChannel,
      deletedChannel = deletedChannel,
      queryChannel = queryChannel,
      answerChannel = answerChannel,
      emptyAnswerChannel = emptyAnswerChannel,
      statusChangedChannel = statusChangedChannel,
      queries = queries
    )
  )
}
