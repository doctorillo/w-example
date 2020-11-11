package bookingtour.protocols.core

/**
  * © Alexey Toroshchin 2019.
  */
package object actors {
  /*final case object Tick
  final case object SendHeartbeat
  final case object WatchHeartbeat
  final case object ReConnect
  final case class SendHeartbeatSession(sessionId: UUID, topic: String)
  final case object TimeOut
  final case object Start
  final case object Stop
  final case object Initialized
  final case object RunTruncate
  final case class SetStamp[A](stamp: A)
  final case class DetachId[A](id: NonEmptyList[A])
  final case class AttachId[A](id: NonEmptyList[A])
  final case class Upserted[A](data: NonEmptyList[A])
  final case class Deleted[A](data: NonEmptyList[A])
  final case class ChannelWaited[A, B](
    id: UUID,
    consumer: ActorRef,
    valueT: Typeable[A],
    idT: Typeable[B],
    idR: Reader[A, B]
  )
  final case class ChannelCreate(id: UUID, tag: String, consumer: ActorRef)
  final case class ChannelDelete(id: UUID, consumer: ActorRef)
  final case class ChannelCreated(
    id: UUID,
    tag: String,
    producer: ActorRef,
    consumer: ActorRef
  )*/

  final case class ArrowResult[A](
      state: List[A],
      deleted: List[A],
      upserted: List[A]
  )

  final case class ArrowTwoResult[A, B, C](
      ch0S: List[A],
      ch1S: List[B],
      outS: List[C],
      deleted: List[C],
      upserted: List[C]
  )
  final case class ArrowThreeResult[A, B, C, D](
      ch0S: List[A],
      ch1S: List[B],
      ch2S: List[C],
      outS: List[D],
      deleted: List[D],
      upserted: List[D]
  )
  final case class ArrowFourResult[A, B, C, D, E](
      ch0S: List[A],
      ch1S: List[B],
      ch2S: List[C],
      ch3S: List[D],
      outS: List[E],
      deleted: List[E],
      upserted: List[E]
  )
  final case class ArrowFiveResult[A, B, C, D, E, F](
      ch0S: List[A],
      ch1S: List[B],
      ch2S: List[C],
      ch3S: List[D],
      ch4S: List[E],
      outS: List[F],
      deleted: List[F],
      upserted: List[F]
  )
  final case class ArrowSixResult[A, B, C, D, E, F, G](
      ch0S: List[A],
      ch1S: List[B],
      ch2S: List[C],
      ch3S: List[D],
      ch4S: List[E],
      ch5S: List[F],
      outS: List[G],
      deleted: List[G],
      upserted: List[G]
  )

  /*final case class ChannelInternalDelete[A](data: NonEmptyList[A])

  final case class ChannelDeleted(id: UUID)*/

  // QUERIES
  /*final case class Fetch(channelId: UUID, replayTo: ActorRef)
  final case class FetchOne[A](channelId: UUID, id: A, replayTo: ActorRef)*/

  // CHANNEL ANSWERS
  /*sealed trait ChannelMessage {
    val channelId: UUID
  }
  final case class CompleteReceived[A](channelId: UUID)
  final case class ChannelEmptySnapshotReceived[A](channelId: UUID) extends ChannelMessage
  final case class ChannelSnapshotReceived[A](channelId: UUID, data: NonEmptyList[A])
      extends ChannelMessage
  final case class ChannelUpdateReceived[A](channelId: UUID, data: NonEmptyList[A])
      extends ChannelMessage
  final case class ChannelDeleteReceived[A](channelId: UUID, data: NonEmptyList[A])
      extends ChannelMessage*/

  // ANSWER
  /*sealed trait Answer {
    val id: UUID
  }

  final case class ErrorReceived(id: UUID, thr: Throwable)     extends Answer
  final case class EmptyMetaReceived(id: UUID, stamp: Instant) extends Answer
  final case class AnswerMetaReceived[A](id: UUID, data: NonEmptyList[A], stamp: Instant)
      extends Answer
  final case class EmptyReceived(id: UUID)                                     extends Answer
  final case class AnswerReceived[A](id: UUID, data: NonEmptyList[A])         extends Answer
  final case class AnswerDeletedReceived[A](id: UUID, data: NonEmptyList[A])  extends Answer
  final case class AnswerUpsertedReceived[A](id: UUID, data: NonEmptyList[A]) extends Answer

  final case class SubscribeCreate(id: UUID, replayTo: ActorRef)
  final case class SubscriptionCreated(id: UUID, producer: ActorRef, consumer: ActorRef)
  final case class StateChanged(id: UUID, replayTo: ActorRef)*/

  /*final object distribution {
    final case class SessionCreate(
      sessionId: UUID,
      targetTag: String,
      consumerTopic: String
    )
    final case class SessionCreated(sessionId: UUID, targetTag: String, consumerTopic: String)
    final case class SessionDelete(sessionId: UUID)
    final case class SessionDeleted(sessionId: UUID)
    final case class HeartbeatReceived(sessionId: UUID, stamp: Instant)
    final case class SessionFetch(sessionId: UUID)
    final case class SessionQuery[A](sessionId: UUID, query: A)
    final case class SessionEmptyReceived(sessionId: UUID)
    final case class SessionAnswerReceived[A](sessionId: UUID, data: NonEmptyList[A])
    final case class SessionStateChangedReceived(sessionId: UUID)

    trait ToJsonOps {
      _: RegisterKey.ToJsonOps =>

      implicit final val sessionCreateEnc: Encoder[SessionCreate]               = deriveEncoder
      implicit final val sessionCreateDec: Decoder[SessionCreate]               = deriveDecoder
      implicit final val sessionCreatedEnc: Encoder[SessionCreated]             = deriveEncoder
      implicit final val sessionCreatedDec: Decoder[SessionCreated]             = deriveDecoder
      implicit final val sessionDeleteEnc: Encoder[SessionDelete]               = deriveEncoder
      implicit final val sessionDeleteDec: Decoder[SessionDelete]               = deriveDecoder
      implicit final val sessionDeletedEnc: Encoder[SessionDeleted]             = deriveEncoder
      implicit final val sessionDeletedDec: Decoder[SessionDeleted]             = deriveDecoder
      implicit final val heartbeatReceivedEnc: Encoder[HeartbeatReceived]       = deriveEncoder
      implicit final val heartbeatReceivedDec: Decoder[HeartbeatReceived]       = deriveDecoder
      implicit final val sessionFetchEnc: Encoder[SessionFetch]                 = deriveEncoder
      implicit final val sessionFetchDec: Decoder[SessionFetch]                 = deriveDecoder
      implicit final val sessionEmptyReceivedEnc: Encoder[SessionEmptyReceived] = deriveEncoder
      implicit final val sessionEmptyReceivedDec: Decoder[SessionEmptyReceived] = deriveDecoder
      implicit final val sessionStateChangedReceivedEnc: Encoder[SessionStateChangedReceived] =
        deriveEncoder
      implicit final val sessionStateChangedReceivedDec: Decoder[SessionStateChangedReceived] =
        deriveDecoder
      implicit final def sessionAnswerReceivedEnc[A](
        implicit enc: Encoder[A]
      ): Encoder[SessionAnswerReceived[A]] = deriveEncoder
      implicit final def sessionAnswerReceivedDec[A](
        implicit dec: Decoder[A]
      ): Decoder[SessionAnswerReceived[A]] = deriveDecoder
      implicit final def sessionQueryEnc[A](
        implicit enc: Encoder[A]
      ): Encoder[SessionQuery[A]] = deriveEncoder
      implicit final def sessionQueryDec[A](
        implicit dec: Decoder[A]
      ): Decoder[SessionQuery[A]] = deriveDecoder
    }

    final object json extends RegisterKey.ToJsonOps with ToJsonOps

    trait ToRegisterKeyOps {
      implicit final val sessionCreateRK: ToRegisterKey[SessionCreate] =
        (attag: universe.WeakTypeTag[SessionCreate]) =>
          RegisterKey.instance(weakT[SessionCreate](attag))
      implicit final val sessionCreatedRK: ToRegisterKey[SessionCreated] =
        (attag: universe.WeakTypeTag[SessionCreated]) =>
          RegisterKey.instance(weakT[SessionCreated](attag))
      implicit final val sessionDeleteRK: ToRegisterKey[SessionDelete] =
        (attag: universe.WeakTypeTag[SessionDelete]) =>
          RegisterKey.instance(weakT[SessionDelete](attag))
      implicit final val sessionDeletedRK: ToRegisterKey[SessionDeleted] =
        (attag: universe.WeakTypeTag[SessionDeleted]) =>
          RegisterKey.instance(weakT[SessionDeleted](attag))
      implicit final val heartbeatReceivedRK: ToRegisterKey[HeartbeatReceived] =
        (attag: universe.WeakTypeTag[HeartbeatReceived]) =>
          RegisterKey.instance(weakT[HeartbeatReceived](attag))
      implicit final val sessionFetchRK: ToRegisterKey[SessionFetch] =
        (attag: universe.WeakTypeTag[SessionFetch]) =>
          RegisterKey.instance(weakT[SessionFetch](attag))
      implicit final val sessionEmptyReceivedRK: ToRegisterKey[SessionEmptyReceived] =
        (attag: universe.WeakTypeTag[SessionEmptyReceived]) =>
          RegisterKey.instance(weakT[SessionEmptyReceived](attag))
      implicit final val sessionStateChangedReceivedRK: ToRegisterKey[SessionStateChangedReceived] =
        (attag: universe.WeakTypeTag[SessionStateChangedReceived]) =>
          RegisterKey.instance(weakT[SessionStateChangedReceived](attag))
      implicit final def sessionQueryRK[A]: ToRegisterKey[SessionQuery[A]] =
        (attag: universe.WeakTypeTag[SessionQuery[A]]) =>
          RegisterKey.instance(weakT[SessionQuery[A]](attag))
      implicit final def sessionAnswerReceivedRK[A]: ToRegisterKey[SessionAnswerReceived[A]] =
        (attag: universe.WeakTypeTag[SessionAnswerReceived[A]]) =>
          RegisterKey.instance(weakT[SessionAnswerReceived[A]](attag))
    }

    final object keys extends ToRegisterKeyOps

    trait ToEntityOps {
      _: ToRegisterKeyOps with ToJsonOps =>
      implicit final val sessionCreateRE: ToRegisterEntity[SessionCreate] =
        (key: ToRegisterKey[SessionCreate], attag: universe.WeakTypeTag[SessionCreate]) =>
          RegisterEntity[SessionCreate](
            k = key.to(attag),
            e = sessionCreateEnc,
            d = sessionCreateDec
          )
      implicit final val sessionCreatedRE: ToRegisterEntity[SessionCreated] =
        (key: ToRegisterKey[SessionCreated], attag: universe.WeakTypeTag[SessionCreated]) =>
          RegisterEntity[SessionCreated](
            k = key.to(attag),
            e = sessionCreatedEnc,
            d = sessionCreatedDec
          )
      implicit final val sessionDeleteRE: ToRegisterEntity[SessionDelete] =
        (key: ToRegisterKey[SessionDelete], attag: universe.WeakTypeTag[SessionDelete]) =>
          RegisterEntity[SessionDelete](
            k = key.to(attag),
            e = sessionDeleteEnc,
            d = sessionDeleteDec
          )
      implicit final val sessionDeletedRE: ToRegisterEntity[SessionDeleted] =
        (key: ToRegisterKey[SessionDeleted], attag: universe.WeakTypeTag[SessionDeleted]) =>
          RegisterEntity[SessionDeleted](
            k = key.to(attag),
            e = sessionDeletedEnc,
            d = sessionDeletedDec
          )
      implicit final val heartbeatReceivedRE: ToRegisterEntity[HeartbeatReceived] =
        (key: ToRegisterKey[HeartbeatReceived], attag: universe.WeakTypeTag[HeartbeatReceived]) =>
          RegisterEntity[HeartbeatReceived](
            k = key.to(attag),
            e = heartbeatReceivedEnc,
            d = heartbeatReceivedDec
          )
      implicit final val sessionFetchRE: ToRegisterEntity[SessionFetch] =
        (key: ToRegisterKey[SessionFetch], attag: universe.WeakTypeTag[SessionFetch]) =>
          RegisterEntity[SessionFetch](
            k = key.to(attag),
            e = sessionFetchEnc,
            d = sessionFetchDec
          )
      implicit final val sessionEmptyReceivedRE: ToRegisterEntity[SessionEmptyReceived] =
        (
          key: ToRegisterKey[SessionEmptyReceived],
          attag: universe.WeakTypeTag[SessionEmptyReceived]
        ) =>
          RegisterEntity[SessionEmptyReceived](
            k = key.to(attag),
            e = sessionEmptyReceivedEnc,
            d = sessionEmptyReceivedDec
          )

      implicit final val sessionStateChangedReceivedRE: ToRegisterEntity[
        SessionStateChangedReceived
      ] =
        (
          key: ToRegisterKey[SessionStateChangedReceived],
          attag: universe.WeakTypeTag[SessionStateChangedReceived]
        ) =>
          RegisterEntity[SessionStateChangedReceived](
            k = key.to(attag),
            e = sessionStateChangedReceivedEnc,
            d = sessionStateChangedReceivedDec
          )

      implicit final def sessionQueryRE[A](
        implicit e: Encoder[A],
        d: Decoder[A]
      ): ToRegisterEntity[SessionQuery[A]] =
        (
          key: ToRegisterKey[SessionQuery[A]],
          attag: universe.WeakTypeTag[SessionQuery[A]]
        ) =>
          RegisterEntity[SessionQuery[A]](
            k = key.to(attag),
            e = sessionQueryEnc(e),
            d = sessionQueryDec(d)
          )

      implicit final def sessionAnswerReceivedRE[A](
        implicit e: Encoder[A],
        d: Decoder[A]
      ): ToRegisterEntity[SessionAnswerReceived[A]] =
        (
          key: ToRegisterKey[SessionAnswerReceived[A]],
          attag: universe.WeakTypeTag[SessionAnswerReceived[A]]
        ) =>
          RegisterEntity[SessionAnswerReceived[A]](
            k = key.to(attag),
            e = sessionAnswerReceivedEnc(e),
            d = sessionAnswerReceivedDec(d)
          )
    }

    final object entities
        extends ToRegisterKeyOps
        with RegisterKey.ToJsonOps
        with ToJsonOps
        with ToEntityOps

    trait ToOrderOps {
      implicit final val sessionCreatedO: Order[SessionCreated] =
        (x: SessionCreated, y: SessionCreated) =>
          CompareOps.compareFn(
            x.sessionId.compareTo(y.sessionId),
            x.targetTag.compareTo(y.targetTag),
            x.consumerTopic.compareTo(y.consumerTopic)
          )
    }

    final object order extends ToOrderOps

  }*/
}
