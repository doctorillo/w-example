package bookingtour.protocols.core.db.enumeration

import scala.collection.immutable

import cats.Order
import enumeratum.values.{CharEnum, CharEnumEntry}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class MssqlChangeOperation(val value: Char) extends CharEnumEntry

case object MssqlChangeOperation extends CharEnum[MssqlChangeOperation] {
  final case object Insert  extends MssqlChangeOperation('I')
  final case object Update  extends MssqlChangeOperation('U')
  final case object Delete  extends MssqlChangeOperation('D')
  final case object Unknown extends MssqlChangeOperation('K')

  override def values: immutable.IndexedSeq[MssqlChangeOperation] = findValues

  trait ToOrderOps {
    implicit final val mssqlChangeOperationO: Order[MssqlChangeOperation] =
      (x: MssqlChangeOperation, y: MssqlChangeOperation) => x.value.compare(y.value)
  }

  final object order extends ToOrderOps

  final object syntax {
    implicit final class MssqlChangeOperationOps(private val value: MssqlChangeOperation) extends AnyVal {
      def toDbEvent: DbEvent = DbEvent.fromMSSQL(value)
    }
  }
}
