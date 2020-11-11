package bookingtour.api.customers.config

import scala.concurrent.duration._

import akka.pattern.extended._
import akka.util.Timeout
import bookingtour.core.actors.cache.{AlgCache, AlgCacheProvider}
import bookingtour.core.actors.modules.AkkaModule
import bookingtour.core.finch.SessionActor
import bookingtour.core.finch.environment.impl.LiveCacheRedis
import bookingtour.protocols.core.cache.{EntityKey, EntityKeyPrefix}
import bookingtour.protocols.core.modules.RuntimeModule
import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue}
import bookingtour.protocols.core.values.enumeration.{AppItem, LangItem}
import bookingtour.protocols.parties.api.sessions.{Attach, Attached, EnvApi}
import io.estatico.newtype.ops._
import zio.{Task, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class SessionModule private (
    implicit rm: RuntimeModule,
    am: AkkaModule,
    cm: CacheModule
) {
  import am._
  import cm._
  import rm._

  implicit private val p: AlgCacheProvider = cacheSessionProvider
  implicit private val sessionCache: AlgCache.Aux[Any, EntityKey, EnvApi] =
    LiveCacheRedis.make[EntityKey, EnvApi](
      "session".coerce[EntityKeyPrefix],
      cacheProvider = p
    )

  private val sessionRef: SessionActor.SessionActorRef =
    SessionActor.ref(entityKey = "state".coerce[EntityKey], enableTrace = true)

  def assignSession(
      sessionId: SessionIdValue,
      fingerprint: FingerprintValue,
      ip: RemoteValue
  ): Task[EnvApi] = {
    ZIO.fromFuture { implicit ec =>
      implicit val t: Timeout = Timeout.durationToTimeout(3.seconds)
      sessionRef.x
        .ask(replayTo =>
          Attach(
            sessionId = sessionId,
            app = AppItem.Partner,
            lang = LangItem.Ru,
            ip = ip,
            fingerprint = fingerprint,
            replayTo = replayTo
          )
        )
        .mapTo[Attached]
    }.map(_.session)
  }
}

object SessionModule {
  final def apply()(
      implicit rm: RuntimeModule,
      am: AkkaModule,
      cm: CacheModule
  ): SessionModule = new SessionModule
}
