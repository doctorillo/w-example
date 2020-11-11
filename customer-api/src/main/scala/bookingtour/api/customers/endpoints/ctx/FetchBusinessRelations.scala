package bookingtour.api.customers.endpoints.ctx

import java.util.UUID

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.api.contexts.queries.FetchBusinessRelationQ
import bookingtour.protocols.core.values.enumeration.{ContextItem, ContextRoleItem}
import bookingtour.protocols.parties.api.PartyValue
import bookingtour.protocols.parties.newTypes.PartyId
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import shapeless.{::, HNil}

/**
  * © Alexey Toroshchin 2019.
  */
final class FetchBusinessRelations private (
    val repository: QueryModule[FetchBusinessRelationQ, PartyValue]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchBusinessRelationQ, PartyValue] {
  val pointPath: String = "relations"

  override val endpoint: Endpoint[IO, FetchBusinessRelationQ] =
    get("api" :: "business" :: pointPath :: path[UUID] :: path[Int] :: path[Int]).map {
      case partyId :: ctxId :: roleId :: HNil =>
        val ctx  = ContextItem.withValue(ctxId)
        val role = ContextRoleItem.withValue(roleId)
        FetchBusinessRelationQ(partyId = PartyId(partyId), ctx = ctx, role = role)
    }
}

object FetchBusinessRelations {
  final def apply(
      repository: QueryModule[FetchBusinessRelationQ, PartyValue]
  )(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[FetchBusinessRelationQ, PartyValue] =
    new FetchBusinessRelations(repository)
}
