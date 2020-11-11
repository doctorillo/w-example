package bookingtour.protocols.doobie.types.mssql

import bookingtour.protocols.core.db.enumeration.MssqlChangeOperation.syntax._
import bookingtour.protocols.core.db.enumeration.{DbEvent, MssqlChangeOperation}
import doobie.util.{Get, Put}

/**
  * © Alexey Toroshchin 2019.
  */
object DbEventMSSQL {
  trait MetaOps {
    implicit final val operationChangeG: Get[DbEvent] =
      Get[String].map(a => MssqlChangeOperation.withValue(a.charAt(0)).toDbEvent)
    implicit final val operationChangeP: Put[DbEvent] =
      Put[String].contramap(_.toMSSQL.value.toString)
  }

  final object mapper extends MetaOps
}
