package bookingtour.protocols.orders.api

import java.time.{LocalDate, LocalDateTime, LocalTime}

import bookingtour.protocols.core.newtypes.quantities.{Minute, Pax}
import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.excursions.newTypes.{ExcursionId, ExcursionOfferId}
import bookingtour.protocols.orders.newTypes.PlannerExcursionItemId
import bookingtour.protocols.parties.api.PointUI
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerExcursionItem(
    id: PlannerExcursionItemId,
    created: LocalDateTime,
    updated: LocalDateTime,
    date: LocalDate,
    excursionId: ExcursionId,
    excursionName: String,
    accommodationPax: Pax,
    excursionOfferId: ExcursionOfferId,
    pickupPoint: PointUI,
    startTime: LocalTime,
    duration: Minute,
    total: Amount,
    clients: List[PlannerExcursionItemClient]
)
