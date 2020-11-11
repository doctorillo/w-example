package bookingtour.protocols.orders

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2020.
  */
object newTypes {
  @newtype final case class PlannerSessionId(x: UUID)
  final object PlannerSessionId {
    implicit val a: UUID => PlannerSessionId = x => PlannerSessionId(x)
    implicit val b: PlannerSessionId => UUID = _.x
  }

  @newtype final case class PlannerSessionPointId(x: UUID)
  final object PlannerSessionPointId {
    implicit val a: UUID => PlannerSessionPointId = x => PlannerSessionPointId(x)
    implicit val b: PlannerSessionPointId => UUID = _.x
  }

  @newtype final case class PlannerClientId(x: UUID)
  final object PlannerClientId {
    implicit val a: UUID => PlannerClientId = x => PlannerClientId(x)
    implicit val b: PlannerClientId => UUID = _.x
  }

  @newtype final case class PlannerRoomId(x: UUID)
  final object PlannerRoomId {
    implicit val a: UUID => PlannerRoomId = x => PlannerRoomId(x)
    implicit val b: PlannerRoomId => UUID = _.x
  }

  @newtype final case class PlannerAccommodationItemId(x: UUID)
  final object PlannerAccommodationItemId {
    implicit val a: UUID => PlannerAccommodationItemId = x => PlannerAccommodationItemId(x)
    implicit val b: PlannerAccommodationItemId => UUID = _.x
  }

  @newtype final case class PlannerExcursionId(x: UUID)
  final object PlannerExcursionId {
    implicit val a: UUID => PlannerExcursionId = x => PlannerExcursionId(x)
    implicit val b: PlannerExcursionId => UUID = _.x
  }

  @newtype final case class PlannerExcursionItemId(x: UUID)
  final object PlannerExcursionItemId {
    implicit val a: UUID => PlannerExcursionItemId = x => PlannerExcursionItemId(x)
    implicit val b: PlannerExcursionItemId => UUID = _.x
  }
}
