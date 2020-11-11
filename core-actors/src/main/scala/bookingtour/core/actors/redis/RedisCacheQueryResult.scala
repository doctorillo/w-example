package bookingtour.core.actors.redis

import java.util.concurrent.TimeUnit

import bookingtour.protocols.core.values.api.QueryResult
import com.twitter.finagle.redis.Client
import com.twitter.finagle.util.DefaultTimer
import com.twitter.io.Buf
import com.twitter.util.{Duration, Future, Timer}
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import zio.Task
import zio.interop.twitter._

/**
  * © Alexey Toroshchin 2019.
  */
final class RedisCacheQueryResult[A, B] private (
    client: Client,
    val dbIndex: Int,
    val sessionKey: String,
    val queryTimeoutMs: Long,
    val expiredAtSeconds: Long
)(implicit val e0: Encoder[A], val e1: Encoder[B], val d0: Decoder[A], val d1: Decoder[B])
    extends CacheQueryResult[A, B] {
  private val duration: Duration    = Duration(queryTimeoutMs, TimeUnit.MILLISECONDS)
  implicit private val timer: Timer = DefaultTimer.getInstance

  def selectDb(): Task[Unit] =
    Task.fromTwitterFuture(Task(client.select(dbIndex).within(duration)))

  def fetchByKey(key: Buf): Task[Option[Buf]] =
    Task.fromTwitterFuture(Task(client.get(key).within(duration)))

  def setEffect(key: Buf, value: QueryResult[B]): Task[Unit] =
    Task.fromTwitterFuture(Task(client.set(key, Buf.Utf8(value.asJson.noSpaces)).within(duration)))

  def setExpiredEffect(key: Buf): Task[Unit] =
    Task.fromTwitterFuture(Task(client.expire(key, expiredAtSeconds))).as(())

  def setExpiredIf(key: Buf, value: Option[Any]): Task[Unit] = {
    if (value.isEmpty) {
      Task.unit
    } else {
      setExpiredEffect(key)
    }
  }

  def decodeIf(value: Option[Buf]): Task[Option[QueryResult[B]]] = value match {
    case Some(Buf.Utf8(buf)) =>
      Task(decode[QueryResult[B]](buf).toOption)
    case None =>
      Task.succeed(None)
  }

  override def bufferByKey(key: A)(
      cb: Either[Throwable, Option[Buf]] => Unit
  ): Unit = {
    val keyBuf = Buf.Utf8(composeKey(key))
    val a: Future[Option[Buf]] = for {
      _     <- client.select(dbIndex).within(duration)
      value <- client.get(keyBuf).within(duration)
      _ <- if (value.isDefined) {
            client.expire(keyBuf, expiredAtSeconds)
          } else {
            Future.value(true)
          }
    } yield value
    a.onSuccess(e => cb(Right(e)))
    a.onFailure(thr => cb(Left(thr)))
  }

  def resultByKey(key: A)(
      cb: Either[
        Throwable,
        Option[QueryResult[B]]
      ] => Unit
  ): Unit = {
    val keyBuf = Buf.Utf8(composeKey(key))
    val a: Future[Either[Throwable, Option[QueryResult[B]]]] = for {
      _     <- client.select(dbIndex).within(duration)
      value <- client.get(keyBuf).within(duration)
      _ <- if (value.isDefined) {
            client.expire(keyBuf, expiredAtSeconds).within(duration)
          } else {
            Future.value(true)
          }
    } yield value match {
      case None =>
        Right(None)

      case Some(Buf.Utf8(result)) =>
        decode[QueryResult[B]](result) match {
          case Left(thr) =>
            Left(thr)
          case Right(x) =>
            Right(Some(x))
        }
    }
    a.onSuccess(e => cb(e))
    a.onFailure(thr => cb(Left(thr)))
  }

  def resultByKeyEffect(key: A): Task[Option[QueryResult[B]]] =
    for {
      keyBuf <- makeKeyBuf(key) <* selectDb()
      result <- fetchByKey(keyBuf).flatMap(decodeIf)
      _      <- setExpiredIf(keyBuf, result)
    } yield result

  override def set(key: A, value: QueryResult[B])(
      cb: Either[Throwable, Unit] => Unit
  ): Unit = {
    val keyBuf = Buf.Utf8(composeKey(key))
    val a = for {
      _ <- client.select(dbIndex).within(duration)
      _ <- client.set(keyBuf, Buf.Utf8(value.asJson.noSpaces)).within(duration)
      _ <- client.expire(keyBuf, expiredAtSeconds).within(duration)
    } yield ()
    runFuture(a)(cb)
  }

  def setEffect(key: A, data: QueryResult[B]): Task[Unit] =
    for {
      keyBuf <- makeKeyBuf(key) <* selectDb()
      _      <- setEffect(keyBuf, data) *> setExpiredEffect(keyBuf)
    } yield ()

  override def setExpired(key: A)(
      cb: Either[Throwable, Unit] => Unit
  ): Unit = {
    val a = for {
      _ <- client.select(dbIndex).within(duration)
      _ <- client.expire(Buf.Utf8(composeKey(key)), expiredAtSeconds).within(duration)
    } yield ()
    runFuture(a)(cb)
  }

  override def delBucket(cb: Either[Throwable, Unit] => Unit): Unit = {
    val a = for {
      _    <- client.select(dbIndex).within(duration)
      keys <- client.keys(Buf.Utf8(matchAllKeys)).within(duration)
      _    <- client.dels(keys).within(duration)
    } yield ()
    runFuture(a)(cb)
  }

  override def del(key: A)(cb: Either[Throwable, Unit] => Unit): Unit = {
    val a = for {
      _ <- client.select(dbIndex).within(duration)
      _ <- client.dels(Seq(Buf.Utf8(composeKey(key)))).within(duration)
    } yield ()
    runFuture(a)(cb)
  }

  override def flushDb(cb: Either[Throwable, Unit] => Unit): Unit = {
    val a = for {
      _ <- client.select(dbIndex).within(duration)
      _ <- client.flushDB().within(duration)
    } yield ()
    runFuture(a)(cb)
  }
}

object RedisCacheQueryResult {
  final def apply[A, B](
      client: Client,
      dbIndex: Int,
      sessionKey: String,
      queryTimeoutMs: Long = 100L,
      expiredAtSeconds: Long = 1800L
  )(
      implicit a: Encoder[A],
      b: Encoder[B],
      c: Decoder[A],
      d: Decoder[B]
  ): RedisCacheQueryResult[A, B] = {
    new RedisCacheQueryResult(
      client = client,
      dbIndex = dbIndex,
      sessionKey = sessionKey,
      queryTimeoutMs = queryTimeoutMs,
      expiredAtSeconds = expiredAtSeconds
    )
  }
}
