package bookingtour.protocols.interlook.source.excursions

import java.time.{LocalDate, LocalTime}

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.WeekDay
import bookingtour.protocols.interlook.source.newTypes.{LookExcursionId, LookPartyId, LookPickupPointId, LookSolverId}
import cats.instances.all._
import cats.syntax.order._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionEP(
    id: LookExcursionId,
    branch: LookPartyId,
    solver: LookSolverId,
    name: String,
    point: LookPickupPointId,
    date: LocalDate,
    time: LocalTime,
    weekDay: WeekDay
)

object ExcursionEP {
  final type Id = LookExcursionId

  implicit final val itemR: ExcursionEP => Id = _.id

  implicit final val itemP: ExcursionEP => Int = _ => 0

  final case class Output(
      id: Int,
      branchId: Int,
      solverId: Int,
      name: String,
      pointId: Int,
      date: LocalDate,
      time: LocalTime,
      weekDay: Int
  )

  implicit final val outputTransform: Output => ExcursionEP = _.into[ExcursionEP]
    .withFieldComputed(_.id, x => LookExcursionId(x.id))
    .withFieldComputed(_.branch, x => LookPartyId(x.branchId))
    .withFieldComputed(_.solver, x => LookSolverId(x.solverId))
    .withFieldComputed(_.point, x => LookPickupPointId(x.pointId))
    .withFieldComputed(_.weekDay, x => {
      if (x.weekDay === 1) {
        WeekDay(7)
      } else {
        WeekDay(x.weekDay - 1)
      }
    })
    .transform
}
