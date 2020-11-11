package bookingtour.api.customers.endpoints.ctx

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.api.PointUI
import bookingtour.protocols.parties.api.queries.FetchPointsQ
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import shapeless.{::, HNil}

/**
  * © Alexey Toroshchin 2019.
  */
final class FetchPoint private (
    val repository: QueryModule[FetchPointsQ.Property, PointUI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchPointsQ.Property, PointUI] {
  val pointPath: String = "points"

  override val endpoint: Endpoint[IO, FetchPointsQ.Property] =
    get("api" :: "ctx" :: pointPath :: path[Int] :: path[Int]).map {
      case ctxId :: langId :: HNil =>
        val lang = LangItem.withValue(langId)
        FetchPointsQ.Property(lang)
    }
}

object FetchPoint {
  final def apply(repository: QueryModule[FetchPointsQ.Property, PointUI])(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[FetchPointsQ.Property, PointUI] =
    new FetchPoint(repository)
}
