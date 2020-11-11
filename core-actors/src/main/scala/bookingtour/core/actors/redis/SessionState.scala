package bookingtour.core.actors.redis

import bookingtour.protocols.core.values.enumeration.AppItem
import cats.data.{Chain, NonEmptyChain}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
trait SessionState[A] {
  val prefix: String
  val dbIndex: Int
  val appKey: AppItem
  val expiredAtHours: Long
  implicit val e0: Encoder[A]
  implicit val d0: Decoder[A]

  implicit val enc: Encoder[W[A]] = io.circe.derivation.deriveEncoder
  implicit val dec: Decoder[W[A]] = io.circe.derivation.deriveDecoder

  protected def composeKey(): String = s"session::$prefix::${appKey.value}"
  def fetch(cb: Either[Throwable, Chain[A]] => Unit): Unit
  def set(data: NonEmptyChain[A])(cb: Either[Throwable, Unit] => Unit): Unit
  def del()(cb: Either[Throwable, Unit] => Unit): Unit
  def flushDb(cb: Either[Throwable, Unit] => Unit): Unit
}
