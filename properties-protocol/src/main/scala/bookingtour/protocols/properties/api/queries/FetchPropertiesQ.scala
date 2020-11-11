package bookingtour.protocols.properties.api.queries

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.newTypes.{ CityId, PartyId }
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class FetchPropertiesQ(lang: LangItem, cityId: CityId, editorPartyId: PartyId)
