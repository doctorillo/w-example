package bookingtour.protocols.parties.api

import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, RoleItem}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class UserV(uuid: UUID, app: AppItem, company: CompanyV, roles: List[RoleItem])
