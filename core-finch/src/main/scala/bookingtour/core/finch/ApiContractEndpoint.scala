package bookingtour.core.finch

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.protocols.core.values.api.QueryResult
import cats.effect.{ContextShift, IO}
import io.finch._

/**
  * © Alexey Toroshchin 2019.
  */
trait ApiContractEndpoint[I, O] {
  val repository: QueryModule[I, O]
  val pointPath: String
  implicit val cs: ContextShift[IO]

  val endpoint: Endpoint[IO, I]

  final def route: Endpoint[IO, QueryResult[O]] =
    endpoint.mapAsync(repository.ask)
}
