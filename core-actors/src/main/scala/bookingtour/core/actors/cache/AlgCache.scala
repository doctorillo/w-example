package bookingtour.core.actors.cache

import cats.data.NonEmptyList
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
trait AlgCache extends Serializable {
  val algCache: AlgCache.Service[Any]
}

object AlgCache {
  final type Aux[R, K, V] = Service[R] {
    type Key   = K
    type Value = V
  }

  trait Service[R] {
    type Key
    type Value

    def get(key: Key): ZIO[R, Throwable, List[Value]]

    def put(key: Key, data: NonEmptyList[Value]): ZIO[R, Throwable, Unit]

    def delete(key: Key): ZIO[R, Throwable, Unit]

    def deleteBucket(): ZIO[R, Throwable, Unit]
  }
}
