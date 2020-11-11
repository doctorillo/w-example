package bookingtour.protocols.doobie.config

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}
import java.util.UUID

import scala.util.matching.Regex

import bookingtour.protocols.core.db.DbEventPayload.{BaseEntity, TruncateEntity}
import bookingtour.protocols.core.register.{RegisterEntity, RegisterEntityCirce}
import bookingtour.protocols.core.values.Ranges
import cats.data.NonEmptyList
import doobie.util.{Get, Put}
import org.postgresql.util.PGobject

/**
  * © Alexey Toroshchin 2019.
  */
trait ToDoobieConfigOps {
  implicit final val truncateEntityLong: RegisterEntity.Aux[TruncateEntity[Long]] =
    RegisterEntityCirce()

  implicit final val truncateEntityInstant: RegisterEntity.Aux[TruncateEntity[Instant]] =
    RegisterEntityCirce()

  implicit final val baseEntityUuidInstant: RegisterEntity.Aux[BaseEntity[UUID, Instant]] =
    RegisterEntityCirce()

  implicit final val baseEntityIntInstant: RegisterEntity.Aux[BaseEntity[Int, Instant]] =
    RegisterEntityCirce()

  private final val dateRangeSchemaType  = "daterange"
  private final val dateRangeSchemaTypes = NonEmptyList.of(dateRangeSchemaType)
  private final val dateRangeRegex: Regex =
    """^\[(\d{4}-\d{2}-\d{2}),(\d{4}-\d{2}-\d{2})""".r("from", "to")

  private final def parseDateRange(a: String): Ranges.Dates =
    (for {
      (from, to) <- dateRangeRegex
                     .findFirstMatchIn(a)
                     .map(m => (m.group("from"), m.group("to")))
    } yield Ranges.Dates(
      LocalDate.parse(from, DateTimeFormatter.ISO_LOCAL_DATE),
      LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE).minusDays(1)
    )).head

  implicit final val dateRangePgPut: Put[Ranges.Dates] = Put.Advanced
    .other[PGobject](dateRangeSchemaTypes)
    .tcontramap[Ranges.Dates] { a =>
      val o = new PGobject
      o.setType(dateRangeSchemaType)
      o.setValue(
        s"[${a.from.format(DateTimeFormatter.ISO_LOCAL_DATE)}, ${a.to.format(DateTimeFormatter.ISO_LOCAL_DATE)}]"
      )
      o
    }

  implicit final val dateRangePgGet: Get[Ranges.Dates] =
    Get.Advanced.other[PGobject](dateRangeSchemaTypes).tmap(a => parseDateRange(a.getValue))

  private final val intRangeSchemaType  = "int4range"
  private final val intRangeSchemaTypes = NonEmptyList.of(intRangeSchemaType)
  private final val intRangeRegex: Regex =
    """(\d*),\s?(\d*)""".r("from", "to")

  private final def parseIntRange(a: String): Ranges.Ints = {
    (for {
      (fromS, toS) <- intRangeRegex
                       .findFirstMatchIn(a)
                       .map(m => (m.group("from"), m.group("to")))
    } yield Ranges.Ints(
      from = fromS.toInt,
      to = toS.toInt - 1
    )).head
  }

  implicit final val intRangePgPut: Put[Ranges.Ints] = Put.Advanced
    .other[PGobject](intRangeSchemaTypes)
    .tcontramap[Ranges.Ints] { a =>
      val o = new PGobject
      o.setType(intRangeSchemaType)
      o.setValue(
        s"[${a.from}, ${a.to}]"
      )
      o
    }

  implicit final val intRangePgGet: Get[Ranges.Ints] =
    Get.Advanced.other[PGobject](intRangeSchemaTypes).tmap(a => parseIntRange(a.getValue))
}
