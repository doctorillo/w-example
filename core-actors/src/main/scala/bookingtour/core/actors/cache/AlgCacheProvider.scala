package bookingtour.core.actors.cache

import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
trait AlgCacheProvider extends Serializable {
  val algCacheProvider: AlgCacheProvider.Service[Any]
}

object AlgCacheProvider {
  trait Service[R] {
    def get(key: String): ZIO[R, Throwable, Option[String]]

    def put[A](key: String, data: String): ZIO[R, Throwable, Unit]

    def delete[A](key: String): ZIO[R, Throwable, Unit]

    def flushDB: ZIO[R, Throwable, Unit]
  }
}
