package bookingtour.protocols.parties.contexts

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{CompanyId, PartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BusinessPartyUI(
    companyId: CompanyId,
    partyId: PartyId,
    code: String,
    name: String,
    contexts: List[BusinessContextUI]
)

object BusinessPartyUI {
  type Id = CompanyId

  implicit val companyUIR: BusinessPartyUI => Id = _.companyId

  implicit final val companyUIPart: BusinessPartyUI => Int = _ => 0
}
