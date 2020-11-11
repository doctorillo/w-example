package bookingtour.core.finch.environment.impl

import java.util.concurrent.TimeUnit

import bookingtour.core.actors.cache.AlgCacheProvider
import bookingtour.protocols.core.cache.CacheStoreKey
import com.twitter.finagle.redis.Client
import com.twitter.finagle.util.DefaultTimer
import com.twitter.io.Buf
import com.twitter.util.{Duration, Timer}
import zio.interop.twitter._
import zio.{Task, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveAlgCacheProvider private (
    val redisClient: Client,
    val storeKey: CacheStoreKey,
    val timeoutMs: Long,
    val entityTTLSeconds: Long
) extends AlgCacheProvider {

  implicit private val timer: Timer = DefaultTimer

  val algCacheProvider: AlgCacheProvider.Service[Any] = new AlgCacheProvider.Service[Any] {
    private def selectDb: Task[Unit] =
      Task.fromTwitterFuture(
        Task
          .effect(redisClient.select(storeKey.x).within(Duration(timeoutMs, TimeUnit.MILLISECONDS)))
      )

    private def entity(key: Buf): Task[Option[String]] =
      Task
        .fromTwitterFuture(
          Task.effect(redisClient.get(key).within(Duration(timeoutMs, TimeUnit.MILLISECONDS)))
        )
        .map {
          case None =>
            None
          case Some(Buf.Utf8(value)) =>
            Some(value)
        }

    private def setTTl(key: Buf): Task[Boolean] =
      Task
        .fromTwitterFuture(
          Task.effect(
            redisClient
              .expire(key, entityTTLSeconds)
              .within(Duration(timeoutMs, TimeUnit.MILLISECONDS))
          )
        )
        .map(_.booleanValue())

    private def saveEntity(key: Buf, value: Buf): Task[Unit] =
      Task.fromTwitterFuture(
        Task.effect(redisClient.set(key, value).within(Duration(timeoutMs, TimeUnit.MILLISECONDS)))
      )

    private def deleteEntity(key: Buf): Task[Long] =
      Task
        .fromTwitterFuture(
          Task.effect(redisClient.dels(Seq(key)).within(Duration(timeoutMs, TimeUnit.MILLISECONDS)))
        )
        .map(_.longValue())

    def get(key: String): ZIO[Any, Throwable, Option[String]] =
      for {
        _ <- selectDb
        a <- Task.effect(Buf.Utf8(key))
        b <- entity(a)
        _ <- ZIO.when(b.isDefined)(setTTl(a))
      } yield b

    def put[A](key: String, data: String): ZIO[Any, Throwable, Unit] =
      for {
        _ <- selectDb
        a <- Task.effect(Buf.Utf8(key))
        b <- Task.effect(Buf.Utf8(data))
        _ <- saveEntity(a, b)
      } yield ()

    def delete[A](key: String): ZIO[Any, Throwable, Unit] =
      for {
        _ <- selectDb
        a <- Task.effect(Buf.Utf8(key))
        _ <- deleteEntity(a)
      } yield ()

    def flushDB: ZIO[Any, Throwable, Unit] = Task.fromTwitterFuture(
      Task.effect(redisClient.flushDB().within(Duration(timeoutMs, TimeUnit.MILLISECONDS)))
    )
  }
}

object LiveAlgCacheProvider {
  final def make(
      redisClient: Client,
      storeKey: CacheStoreKey,
      timeoutMs: Long,
      entityTTLSeconds: Long
  ): AlgCacheProvider = new LiveAlgCacheProvider(redisClient, storeKey, timeoutMs, entityTTLSeconds)
}
