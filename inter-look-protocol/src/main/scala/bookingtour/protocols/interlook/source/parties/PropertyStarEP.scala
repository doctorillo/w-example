package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.PropertyStar
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookPropertyStarId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyStarEP(
    id: LookPropertyStarId,
    property: LookPartyId,
    star: PropertyStar
)

object PropertyStarEP {
  type Id = LookPropertyStarId

  implicit final val itemR: PropertyStarEP => Id = _.id

  implicit final val itemP: PropertyStarEP => Int = _ => 0

  final case class Output(id: Int, property: Int, star: String)

  implicit final val outputTransform: Output => PropertyStarEP = _.into[PropertyStarEP]
    .withFieldComputed(_.id, x => LookPropertyStarId(x.id))
    .withFieldComputed(_.property, x => LookPartyId(x.property))
    .withFieldComputed(
      _.star,
      x =>
        PropertyStar(
          "^(\\d)".r
            .findFirstIn(x.star)
            .map(_.toInt)
            .getOrElse(2)
        )
    )
    .transform
}
