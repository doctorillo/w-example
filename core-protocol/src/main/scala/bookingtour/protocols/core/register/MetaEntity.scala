package bookingtour.protocols.core.register

import scala.util.Try

import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import shapeless._
import shapeless.ops.hlist

/**
  * © Alexey Toroshchin 2019.
  */
trait MetaEntity[A] {
  type R
  type L = Encoder[R] :: Decoder[R] :: HNil
  val key: RegisterKey
  val meta: L
  val es: hlist.Selector[L, Encoder[A]]
  val ds: hlist.Selector[L, Decoder[A]]
  final def encode(i: A): Either[Throwable, String] =
    Try(i.asJson(meta.select(es)).noSpaces).toEither
  final def decode(i: String): Either[Throwable, A] = io.circe.parser.decode(i)(meta.select(ds))
}

object MetaEntity {
  def apply[T](k: RegisterKey)(implicit e: Encoder[T], d: Decoder[T]): MetaEntity[T] =
    new MetaEntity[T] {
      override type R = T
      override val key: RegisterKey                       = k
      override val meta: Encoder[T] :: Decoder[T] :: HNil = e :: d :: HNil
      override val es: hlist.Selector[L, Encoder[T]]      = the[hlist.Selector[L, Encoder[T]]]
      override val ds: hlist.Selector[L, Decoder[T]]      = the[hlist.Selector[L, Decoder[T]]]
    }
}
