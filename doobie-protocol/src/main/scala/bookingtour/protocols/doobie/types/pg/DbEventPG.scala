package bookingtour.protocols.doobie.types.pg

import bookingtour.protocols.core.db.enumeration.PgDbEvent.syntax._
import bookingtour.protocols.core.db.enumeration.{DbEvent, PgDbEvent}
import doobie.util.{Get, Put}

/**
  * © Alexey Toroshchin 2019.
  */
object DbEventPG {
  trait MetaOps {
    implicit final val dbEventPgG: Get[DbEvent] =
      Get[String].map(a => PgDbEvent.withValue(a).toDbEvent)
    implicit final val dbEventPgP: Put[DbEvent] =
      Put[String].contramap(_.toPG.value.toString)
  }

  final object mapper extends MetaOps
}
