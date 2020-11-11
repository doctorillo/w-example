package bookingtour.protocols.core.values.api

import derevo.circe.{decoder, encoder}
import derevo.derive
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder)
final case class QueryResult[A](
    items: List[A],
    size: Long,
    hasError: Boolean = false,
    debug: List[String] = List.empty
)

object QueryResult {
  final type TaskResult[A] = ZIO[Any, Throwable, QueryResult[A]]

  final def empty[A]: QueryResult[A] = QueryResult(items = List.empty, size = 0)

  final def one[A](item: A): QueryResult[A] = QueryResult(items = List(item), size = 1)

  final def fromList[A](list: List[A]): QueryResult[A] =
    QueryResult(items = list, size = list.length)
}
