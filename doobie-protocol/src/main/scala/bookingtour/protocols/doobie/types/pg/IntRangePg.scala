package bookingtour.protocols.doobie.types.pg

import scala.util.matching.Regex

import bookingtour.protocols.core.values.Ranges
import cats.data.NonEmptyList
import doobie.util.{Get, Put}
import org.postgresql.util.PGobject

/**
  * Created by d0ct0r on 2019-11-02.
  */
object IntRangePg {
  final object mapper {
    private val schemaType  = "int4range"
    private val schemaTypes = NonEmptyList.of(schemaType)
    private val regex: Regex =
      """(\d*),\s?(\d*)""".r("from", "to")
    private def parse(a: String): Ranges.Ints =
      (for {
        (from: String, to) <- regex
                               .findFirstMatchIn(a)
                               .map(m => (m.group("from"), m.group("to")))
      } yield Ranges.Ints(
        from = from.toInt,
        to = to.toInt
      )).head

    implicit val dateRangePgPut: Put[Ranges.Ints] = Put.Advanced
      .other[PGobject](schemaTypes)
      .tcontramap[Ranges.Ints] { a =>
        val o = new PGobject
        o.setType(schemaType)
        o.setValue(
          s"[${a.from}, ${a.to - 1}]"
        )
        o
      }
    implicit val dateRangePgGet: Get[Ranges.Ints] =
      Get.Advanced.other[PGobject](schemaTypes).tmap(a => parse(a.getValue))
  }
}
