package bookingtour.protocols.api.algebra

import java.util.UUID

import bookingtour.protocols.api.contexts.queries.FetchBusinessRelationQ
import bookingtour.protocols.parties.api.PartyValue
import cats.data.Chain

/**
  * © Alexey Toroshchin 2019.
  */
trait PartyTreeAlgebra[F[_]] {
  def relationId(
      query: FetchBusinessRelationQ
  ): F[Chain[UUID]]

  def partyLabels(
      id: Chain[UUID]
  ): F[Chain[PartyValue]]
}
