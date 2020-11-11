package bookingtour.protocols.core

import io.circe.derivation._
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
package object cache {
  @newtype final case class CacheStoreKey(x: Int)
  @newtype final case class EntityKeyPrefix(x: String)

  @newtype final case class EntityKey(x: String)
  @newtype final case class EntityTTL(x: Long)

  final case class EntityWrapper[A](data: List[A])
  final object EntityWrapper {
    implicit def entityWrapperEnc[A: Encoder]: Encoder[EntityWrapper[A]] = deriveEncoder
    implicit def entityWrapperDec[A: Decoder]: Decoder[EntityWrapper[A]] = deriveDecoder
  }
}
