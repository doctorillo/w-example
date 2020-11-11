package bookingtour.protocols.doobie.types.pg

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.matching.Regex

import bookingtour.protocols.core.values.Ranges
import cats.data.NonEmptyList
import doobie.util.{Get, Put}
import org.postgresql.util.PGobject

/**
  * Created by d0ct0r on 2019-11-02.
  */
object DateRangePg {
  final object mapper {
    private val schemaType  = "daterange"
    private val schemaTypes = NonEmptyList.of(schemaType)
    private val regex: Regex =
      """^\[(\d{4}-\d{2}-\d{2}),(\d{4}-\d{2}-\d{2})""".r("from", "to")

    private def parse(a: String): Ranges.Dates =
      (for {
        (from, to) <- regex
                       .findFirstMatchIn(a)
                       .map(m => (m.group("from"), m.group("to")))
      } yield Ranges.Dates(
        LocalDate.parse(from, DateTimeFormatter.ISO_LOCAL_DATE),
        LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE).minusDays(1)
      )).head

    implicit val dateRangePgPut: Put[Ranges.Dates] = Put.Advanced
      .other[PGobject](schemaTypes)
      .tcontramap[Ranges.Dates] { a =>
        val o = new PGobject
        o.setType(schemaType)
        o.setValue(
          s"[${a.from.format(DateTimeFormatter.ISO_LOCAL_DATE)}, ${a.to.format(DateTimeFormatter.ISO_LOCAL_DATE)}]"
        )
        o
      }
    implicit val dateRangePgGet: Get[Ranges.Dates] =
      Get.Advanced.other[PGobject](schemaTypes).tmap(a => parse(a.getValue))
  }
}
