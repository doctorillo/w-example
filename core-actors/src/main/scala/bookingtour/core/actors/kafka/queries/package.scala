package bookingtour.core.actors.kafka

/**
  * © Alexey Toroshchin 2019.
  */
package object queries {
  /*final def makeEnvelope(
    taggedChannel: Option[TaggedChannel],
    office: PostOffice,
    bodyKey: RegisterKey,
    targetTopic: String,
    replayToTopic: String,
    messageTtl: Long
  ): UIO[MessageEnvelope] =
    ZIO.effectTotal(
      MessageEnvelope(
        id = UUID.randomUUID(),
        channel = taggedChannel,
        bodyKey = bodyKey,
        stamps = NonEmptyList.one(
          PostStamp(
            office = office,
            targetTopic = targetTopic,
            replayToTopic = Some(replayToTopic)
          )
        ),
        expiredAt = Instant.now().plusSeconds(messageTtl)
      )
    )

  private final def sessionIdEffect(envelope: MessageEnvelope): Task[UUID] =
    ZIO.effect(
      envelope.channel
        .flatMap(_.id)
        .get
    )

  final def makeEnvelopeFromReceived(
    envelope: MessageEnvelope,
    office: PostOffice,
    bodyKey: RegisterKey,
    messageTtl: Long
  ): Task[MessageEnvelope] = {
    for {
      sessionId <- sessionIdEffect(envelope)
      consumerTopic <- ZIO
        .effectTotal(envelope.stamps.last.replayToTopic.get)
    } yield MessageEnvelope(
      id = envelope.id,
      channel = envelope.channel.map(_.copy(id = Some(sessionId))),
      bodyKey = bodyKey,
      stamps = NonEmptyList.one(
        PostStamp(
          office = office,
          targetTopic = consumerTopic,
          replayToTopic = None
        )
      ),
      expiredAt = Instant.now().plusSeconds(messageTtl)
    )
  }

  final def makeEnvelopeFromError(
    envelope: MessageEnvelope,
    office: PostOffice,
    bodyKey: RegisterKey,
    errors: Chain[String],
    messageTtl: Long
  ): Task[MessageEnvelope] = {
    for {
      sessionId <- sessionIdEffect(envelope)
      consumerTopic <- ZIO
        .effectTotal(envelope.stamps.last.replayToTopic.get)
    } yield MessageEnvelope(
      id = envelope.id,
      channel = envelope.channel.map(_.copy(id = Some(sessionId))),
      bodyKey = bodyKey,
      stamps = NonEmptyList.one(
        PostStamp(
          office = office,
          targetTopic = consumerTopic,
          replayToTopic = None,
          errors = errors
        )
      ),
      expiredAt = Instant.now().plusSeconds(messageTtl)
    )
  }*/
}
