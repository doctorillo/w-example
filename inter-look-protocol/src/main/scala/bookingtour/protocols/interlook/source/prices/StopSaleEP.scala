package bookingtour.protocols.interlook.source.prices

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Month, Year}
import bookingtour.protocols.core.values.enumeration.StopSaleItem
import bookingtour.protocols.interlook.source.newTypes._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class StopSaleEP(
    id: LookStopSaleId,
    date: LocalDate,
    stopType: StopSaleItem,
    combination: ServiceCombinationEP,
    allRoomTypes: LookStopAllRoomTypes = false,
    allRoomCategories: LookStopAllRoomCategories = false,
    canceled: LookStopCanceled,
    stamp: Instant
)

object StopSaleEP {
  type Id = LookStopSaleId

  implicit final val itemR0: StopSaleEP => Id = _.id

  implicit final val itemP0: StopSaleEP => StopSaleEKey = x =>
    StopSaleEKey(
      x.combination.property,
      month = Month(x.date.getMonthValue),
      year = Year(x.date.getYear)
    )

  final case class Output(
      id: Int,
      propertyId: Int,
      date: LocalDate,
      stopType: StopSaleItem,
      allRoomTypes: Boolean,
      allRoomCategories: Boolean,
      canceled: Option[LocalDateTime],
      serviceClassId: Int,
      roomTypeId: Int,
      roomCategoryId: Int,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => StopSaleEP = _.into[StopSaleEP]
    .withFieldComputed(_.id, x => LookStopSaleId(x.id))
    .withFieldComputed(
      _.combination,
      x =>
        ServiceCombinationEP(
          id = LookServiceCombinationId(x.serviceClassId),
          property = LookPartyId(x.propertyId),
          typeRoom = LookRoomTypeId(x.roomTypeId),
          categoryRoom = LookRoomCategoryId(x.roomCategoryId)
        )
    )
    .withFieldComputed(_.allRoomTypes, x => LookStopAllRoomTypes(x.allRoomTypes))
    .withFieldComputed(_.allRoomCategories, x => LookStopAllRoomCategories(x.allRoomCategories))
    .withFieldComputed(_.canceled, x => LookStopCanceled(x.canceled.isDefined))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform
}
