package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.LookCustomerGroupId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PartnerGroupEP(
    id: LookCustomerGroupId,
    name: String
)

object PartnerGroupEP {
  type Id = LookCustomerGroupId

  implicit final val itemR: PartnerGroupEP => Id = _.id

  implicit final val itemP: PartnerGroupEP => Int = _ => 0

  final case class Output(
      id: Int,
      name: String
  )

  implicit final val outputTransform: Output => PartnerGroupEP = _.into[PartnerGroupEP]
    .withFieldComputed(_.id, x => LookCustomerGroupId(x.id))
    .transform
}
