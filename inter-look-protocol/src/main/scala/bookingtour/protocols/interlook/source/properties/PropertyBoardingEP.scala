package bookingtour.protocols.interlook.source.properties

import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyBoardingEP(id: BoardingKeyEP, boarding: BoardingEP)

object PropertyBoardingEP {
  type Id = BoardingKeyEP

  implicit final val itemR0: PropertyBoardingEP => Id = _.id

  implicit final val itemP0: PropertyBoardingEP => Int = _ => 0
}
