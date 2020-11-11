package bookingtour.protocols.orders.env

import java.util.UUID

import bookingtour.protocols.orders.api.PlannerProperty
import zio.{UIO, Has}

/**
  * © Alexey Toroshchin 2020.
  */
object PlannerSessionOrderProperty {
  type HasService = Has[PlannerSessionOrderProperty.Service]

  trait Service {
    def book(bookingId: UUID, session: PlannerProperty): UIO[Option[UUID]]
  }

}
