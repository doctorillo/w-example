package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookBoardingId, LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BoardingKeyEP(property: LookPartyId, boarding: LookBoardingId)

object BoardingKeyEP {
  type Id = BoardingKeyEP

  implicit final val itemR: BoardingKeyEP => Id = x => x

  implicit final val itemP: BoardingKeyEP => Int = _ => 0
}
