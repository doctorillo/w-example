package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.core.values.enumeration.SyncItem._
import bookingtour.protocols.interlook.source.newTypes.{LookBoardingId, LookPartyId}
import bookingtour.protocols.properties.api.{BoardingProduct, PropertyCardProduct}
import bookingtour.protocols.properties.newTypes.{BoardingId, PropertyId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class BoardingOp(
    id: BoardingId,
    boardingSync: LookBoardingId,
    property: PropertyId,
    propertySync: LookPartyId
)

object BoardingOp {
  final type Id = BoardingId

  implicit final val itemR0: BoardingOp => Id = _.id

  implicit final val itemP0: BoardingOp => Int = _ => 0

  implicit final val itemT0: (List[SyncItem], BoardingProduct) => BoardingOp = (propertySyncs, x) =>
    x.into[BoardingOp]
      .withFieldComputed(_.propertySync, _ => LookPartyId(asInterLook(propertySyncs).head.id))
      .withFieldComputed(_.boardingSync, x => LookBoardingId(asInterLook(x.syncs).head.id))
      .transform

  implicit final val itemT1: PropertyCardProduct => List[BoardingOp] = x =>
    x.boardings.map(z => itemT0(x.propertySync, z))
}
