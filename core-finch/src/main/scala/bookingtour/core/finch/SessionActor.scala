package bookingtour.core.finch

import java.time.Instant

import SessionActor.protocols.SessionRemove
import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.cache.AlgCache
import bookingtour.protocols.core._
import bookingtour.protocols.core.cache.EntityKey
import bookingtour.protocols.core.sessions.SessionIdValue
import bookingtour.protocols.parties.api.sessions.EnvApi.Guest
import bookingtour.protocols.parties.api.sessions.{Attach, AttachAuthorized, EnvApi, UnitReceived}
import bookingtour.protocols.parties.api.sessions.{Attach, Attached, EnvApi, UnitReceived}
import cats.Order
import cats.data.NonEmptyList
import cats.instances.all._
import cats.syntax.order._
import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
final class SessionActor private (
    entityKey: EntityKey,
    enableTrace: Boolean
)(implicit zioRuntime: zio.Runtime[zio.ZEnv], cache: AlgCache.Aux[Any, EntityKey, EnvApi])
    extends Actor with Stash with ActorLogging {
  private val tag: String = "session-actor"

  override def preStart(): Unit = {
    super.preStart()
    zioRuntime.unsafeRunAsync(cache.get(entityKey)) {
      case zio.Exit.Failure(cause) =>
        cause.failures.foreach(thr => log.error(s"$tag. pre-start. {}.", thr))
        context.stop(self)

      case zio.Exit.Success(value) =>
        unstashAll()
        switch(value)
    }
  }

  private def attach(state: List[EnvApi], item: EnvApi): Unit = {
    val xs = state.filterNot(_.sessionId === item.sessionId) :+ item
    zioRuntime.unsafeRunAsync(
      cache.put(entityKey, NonEmptyList.fromListUnsafe(xs))
    ) {
      case zio.Exit.Failure(cause) =>
        cause.failures.foreach(log.error(s"$tag. attach. {},", _))

      case zio.Exit.Success(_) =>
    }
  }

  private def dettach(state: List[EnvApi], item: EnvApi): Unit = {
    val xs = state.filterNot(_.sessionId === item.sessionId)
    if (xs.isEmpty) {
      zioRuntime.unsafeRunAsync(cache.delete(entityKey)) {
        case zio.Exit.Failure(cause) =>
          cause.failures.foreach(log.error(s"$tag. attach. {},", _))

        case zio.Exit.Success(_) =>
      }
    } else {
      zioRuntime.unsafeRunAsync(
        cache.put(entityKey, NonEmptyList.fromListUnsafe(xs))
      ) {
        case zio.Exit.Failure(cause) =>
          cause.failures.foreach(log.error(s"$tag. attach. {},", _))

        case zio.Exit.Success(_) =>
      }
    }
  }

  private def switch(state: List[EnvApi]): Unit =
    context.become(behaviors(state))

  private def behaviors(
      state: List[EnvApi]
  ): Receive = {
    case SessionRemove(sessionId) =>
      if (enableTrace) {
        log.info(s"$tag. receive session-remove.")
      }
      state.find(_.sessionId === sessionId) match {
        case Some(item) =>
          dettach(state, item)
          switch(state.filterNot(_.sessionId === sessionId))

        case None =>
          log.error(s"$tag. receive session-remove. id not found.")
      }

    case AttachAuthorized(sessionId, env, replayTo) =>
      state.find(_.sessionId === sessionId) match {
        case Some(ctx) =>
          if (enableTrace) {
            log.info(s"$tag. receive attach-authorized. ${env.email}.")
          }
          val item = EnvApi.Authorized(
            sessionId = sessionId,
            app = ctx.app,
            uiLang = ctx.uiLang,
            ip = ctx.ip,
            fingerprint = ctx.fingerprint,
            context = env,
            created = Instant.now()
          )
          attach(state, item)
          switch(state.filterNot(_.sessionId === sessionId) :+ item)

        case None =>
          log.error(s"$tag. session not found.")
      }
      replayTo ! UnitReceived

    case Attach(sessionId, app, lang, ip, fingerprint, replayTo) =>
      state.find(_.sessionId === sessionId) match {
        case Some(x) =>
          replayTo ! Attached(sessionId, x)

        case None =>
          val item = Guest(
            sessionId = sessionId,
            app = app,
            uiLang = lang,
            ip = ip,
            fingerprint = fingerprint,
            created = Instant.now()
          )
          attach(state, item)
          switch(state :+ item)
          replayTo ! Attached(sessionId, item)
      }

    case msg =>
      log.info(s"$tag. undefined $msg")
  }

  def receive: Receive = {
    case _ =>
      stash()
  }
}

object SessionActor {
  import akka.actor.{ActorRef, ActorSystem, Props}
  import bookingtour.protocols.parties.api.sessions.EnvApi
  import io.estatico.newtype.ops._

  @newtype final case class SessionActorRef(x: ActorRef)

  final object SessionActorRef {
    implicit val sessionActorRefO: Order[SessionActorRef] =
      (x: SessionActorRef, y: SessionActorRef) => x.x.compareTo(y.x)
  }
  final object protocols {
    final case class SessionRemove(id: SessionIdValue)
    final case class AddGuest(env: EnvApi.Guest)
    final case class AddAuthorized(env: EnvApi.Authorized)
  }

  final def ref(
      entityKey: EntityKey,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      cache: AlgCache.Aux[Any, EntityKey, EnvApi]
  ): SessionActorRef =
    ctx
      .actorOf(Props(new SessionActor(entityKey, enableTrace)), entityKey.x)
      .coerce[SessionActorRef]
}
