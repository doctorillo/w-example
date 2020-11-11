package bookingtour.core.actors.redis

import bookingtour.protocols.core.values.api.QueryResult
import com.twitter.io.Buf
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import zio.Task

/**
  * © Alexey Toroshchin 2019.
  */
trait CacheQueryResult[A, B] {
  val dbIndex: Int
  val sessionKey: String
  val queryTimeoutMs: Long
  val expiredAtSeconds: Long
  implicit val e0: Encoder[A]
  implicit val e1: Encoder[B]
  implicit val d0: Decoder[A]
  implicit val d1: Decoder[B]

  /*implicit val resultEnc: Encoder[QueryResult[B]] = QueryResult.queryResultEnc[B]
  implicit val resultDec: Decoder[QueryResult[B]] = QueryResult.queryResultDec[B]*/

  def composeKey(key: A): String = s"$sessionKey::${key.asJson.noSpaces}"
  def matchAllKeys: String       = s"$sessionKey::*"

  def makeKeyBuf(key: A): Task[Buf] = Task.effect(Buf.Utf8(composeKey(key)))

  def bufferByKey(key: A)(cb: Either[Throwable, Option[Buf]] => Unit): Unit
  def resultByKey(key: A)(cb: Either[Throwable, Option[QueryResult[B]]] => Unit): Unit

  def resultByKeyEffect(key: A): Task[Option[QueryResult[B]]]
  def set(key: A, data: QueryResult[B])(cb: Either[Throwable, Unit] => Unit): Unit

  def setEffect(key: A, data: QueryResult[B]): Task[Unit]
  def setExpired(key: A)(cb: Either[Throwable, Unit] => Unit): Unit
  def delBucket(cb: Either[Throwable, Unit] => Unit): Unit
  def del(key: A)(cb: Either[Throwable, Unit] => Unit): Unit
  def flushDb(cb: Either[Throwable, Unit] => Unit): Unit
}
