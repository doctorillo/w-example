package bookingtour.core.finch.environment.impl

import bookingtour.core.actors.cache.{AlgCache, AlgCacheProvider}
import bookingtour.protocols.core.cache._
import cats.data.NonEmptyList
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.ops._
import zio.{Task, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveCacheRedis[K, V] private (
    val entityKeyPrefix: EntityKeyPrefix,
    val provider: AlgCacheProvider.Service[Any]
)(
    implicit val keyEncoder: Encoder[K],
    val keyDecoder: Decoder[K],
    val valueEncoder: Encoder[V],
    val valueDecoder: Decoder[V]
) extends AlgCache {

  val algCache: AlgCache.Service[Any] = new AlgCache.Service[Any] {
    final type Key   = K
    final type Value = V

    implicit private final def e2: Encoder[EntityWrapper[V]] =
      EntityWrapper.entityWrapperEnc[V]

    implicit private final def d2: Decoder[EntityWrapper[V]] =
      EntityWrapper.entityWrapperDec[V]

    private def composeKey(key: EntityKey): Task[String] =
      Task.effect(s"${entityKeyPrefix.x}::${key.x}")

    private def composeAllKeys(): Task[String] = Task.effectTotal(s"${entityKeyPrefix.x}::*")

    private def encodeKey(key: K): Task[String] =
      Task.effect(key.asJson.noSpaces)

    private def encode(data: List[V]): Task[String] =
      Task.effect(EntityWrapper[V](data).asJson.noSpaces)

    private def decode(value: Option[String]): Task[List[V]] =
      value match {
        case None =>
          ZIO.effectTotal(List.empty[V])

        case Some(value) =>
          io.circe.parser.decode[EntityWrapper[V]](value) match {
            case Left(err) =>
              ZIO.fail(err.fillInStackTrace())
            case Right(x) =>
              ZIO.effectTotal(x.data)
          }
      }

    def get(key: K): ZIO[Any, Throwable, List[V]] =
      for {
        k <- encodeKey(key).map(_.coerce[EntityKey])
        a <- composeKey(k)
        b <- provider.get(a)
        c <- decode(b)
      } yield c

    def put(key: K, data: NonEmptyList[V]): ZIO[Any, Throwable, Unit] =
      for {
        entityKey <- encodeKey(key).map(_.coerce[EntityKey])
        a         <- composeKey(entityKey)
        b         <- encode(data.toList)
        c         <- provider.put(a, b)
      } yield c

    def delete(key: K): ZIO[Any, Throwable, Unit] =
      for {
        a <- encodeKey(key).map(_.coerce[EntityKey])
        b <- composeKey(a)
        _ <- provider.delete(b)
      } yield ()

    def deleteBucket(): ZIO[Any, Throwable, Unit] = composeAllKeys().flatMap(provider.delete)
  }
}

object LiveCacheRedis {
  final def make[Key, Value](prefix: EntityKeyPrefix, cacheProvider: AlgCacheProvider)(
      implicit ee0: Encoder[Key],
      dd0: Decoder[Key],
      ee1: Encoder[Value],
      dd1: Decoder[Value]
  ): AlgCache.Aux[Any, Key, Value] =
    new LiveCacheRedis[Key, Value](prefix, cacheProvider.algCacheProvider).algCache
      .asInstanceOf[AlgCache.Aux[Any, Key, Value]]
}
