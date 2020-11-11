package bookingtour.protocols.api.business.business

import java.util.UUID

import bookingtour.protocols.parties.agg.basic.CompanyAgg
import cats.implicits._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class GroupMemberAGG(id: UUID, groupId: UUID, company: CompanyAgg)
