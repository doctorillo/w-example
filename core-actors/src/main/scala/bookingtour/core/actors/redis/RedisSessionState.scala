package bookingtour.core.actors.redis

import bookingtour.protocols.core.values.enumeration.AppItem
import cats.data.{Chain, NonEmptyChain}
import com.twitter.finagle.redis.Client
import com.twitter.io.Buf
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final class RedisSessionState[A] private (
    client: Client,
    val prefix: String,
    val dbIndex: Int,
    val appKey: AppItem,
    val expiredAtHours: Long
)(implicit val e0: Encoder[A], val d0: Decoder[A])
    extends SessionState[A] {
  def fetch(cb: Either[Throwable, Chain[A]] => Unit): Unit = {
    val keyBuf = Buf.Utf8(composeKey())
    for {
      _     <- client.select(dbIndex)
      value <- client.get(keyBuf)
      _     <- client.expire(keyBuf, expiredAtHours * 3600)
    } value match {
      case None =>
        cb(Right(Chain.empty[A]))

      case Some(Buf.Utf8(result)) =>
        decode[W[A]](result) match {
          case Left(thr) =>
            cb(Left(thr))

          case Right(w) =>
            cb(Right(w.xs.toChain))
        }
    }
  }

  def set(data: NonEmptyChain[A])(cb: Either[Throwable, Unit] => Unit): Unit = {
    val keyBuf = Buf.Utf8(composeKey())
    val a = for {
      _ <- client.select(dbIndex)
      _ <- client.set(keyBuf, Buf.Utf8(W(data).asJson.noSpaces))
      _ <- client.expire(keyBuf, expiredAtHours * 3600)
    } yield ()
    runFuture(a)(cb)
  }

  def del()(cb: Either[Throwable, Unit] => Unit): Unit = {
    val a = for {
      _ <- client.select(dbIndex)
      _ <- client.dels(Seq(Buf.Utf8(composeKey())))
    } yield ()
    runFuture(a)(cb)
  }

  def flushDb(cb: Either[Throwable, Unit] => Unit): Unit = {
    val a = for {
      _ <- client.select(dbIndex)
      _ <- client.flushDB()
    } yield ()
    runFuture(a)(cb)
  }
}

object RedisSessionState {
  final def apply[A](
      client: Client,
      prefix: String,
      dbIndex: Int,
      appKey: AppItem,
      expiredAtHours: Long = 48L
  )(
      implicit e0: Encoder[A],
      d0: Decoder[A]
  ): SessionState[A] = new RedisSessionState[A](
    client,
    prefix,
    dbIndex,
    appKey,
    expiredAtHours
  )
}
