package bookingtour.protocols.core.values

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.Age
import cats.syntax.option._
import cats.~>
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable
import cats.instances.all._

/**
  * Created by d0ct0r on 2019-10-31.
  */
@derive(encoder, decoder, order, loggable)
sealed abstract class Ranges extends Product with Serializable

object Ranges {
  @derive(encoder, decoder, order, loggable)
  final case class Dates(from: LocalDate, to: LocalDate) extends Ranges

  @derive(encoder, decoder, order, loggable)
  final case class Ints(from: Int, to: Int) extends Ranges

  final val InfantAge: Ranges.Ints = Ranges.Ints(0, 1)

  final val AdultAge: Ranges.Ints = Ranges.Ints(17, 150)

  final object Dates {
    def fromList(xs: List[LocalDate]): List[Ranges.Dates] = {
      xs.sorted.foldLeft(List.empty[Ranges.Dates]) { (a, x) =>
        a.lastOption match {
          case None =>
            List(Ranges.Dates(x, x))

          case Some(dates) if dates.to.plusDays(1).isEqual(x) =>
            a :+ dates.copy(to = x)

          case Some(_) =>
            a :+ Ranges.Dates(x, x)
        }
      }
    }

    def fromList(xs: List[LocalDate], excludes: List[Ranges.Dates]): List[Ranges.Dates] = {
      xs.filterNot(x => excludes.exists(_.dateInRange(x)))
        .sorted
        .foldLeft(List.empty[Ranges.Dates]) { (a, x) =>
          a.lastOption match {
            case None =>
              List(Ranges.Dates(x, x))

            case Some(dates) if dates.to.plusDays(1).isEqual(x) =>
              a :+ dates.copy(to = x)

            case Some(_) =>
              a :+ Ranges.Dates(x, x)
          }
        }
    }

    /*implicit val dateRangeEnc: Encoder[Ranges.Dates] = deriveEncoder
    implicit val dateRangeDec: Decoder[Ranges.Dates] = deriveDecoder
    implicit val dateRangeO: Order[Ranges.Dates] =
      (x: Ranges.Dates, y: Ranges.Dates) => {
        val f = x.from.compareTo(y.from)
        if (f =!= 0) {
          f
        } else {
          x.to.compareTo(y.to)
        }
      }*/

    implicit final class RangesDateOps(private val self: Ranges.Dates) extends AnyVal {
      def nights: Int = ChronoUnit.DAYS.between(self.from, self.to).toInt

      def daysInclusive: Int = ChronoUnit.DAYS.between(self.from, self.to.plusDays(1)).toInt

      def dateInRange(d: LocalDate): Boolean =
        self.from.isEqual(d) || self.to.isEqual(d) || (self.from.isBefore(d) && self.to.isAfter(
          d
        ))

      def intersected(other: Ranges.Dates): Boolean =
        fromPeriodInclusive(other.from, other.to) || toPeriodInclusive(other.from, other.to) || datesIncluded(
          other.from,
          other.to
        )

      def fromPeriodInclusive(f: LocalDate, t: LocalDate): Boolean =
        self.from.isEqual(f) || self.from.isEqual(t) || (self.from.isAfter(f) && self.from
          .isBefore(t))

      def toPeriodInclusive(f: LocalDate, t: LocalDate): Boolean =
        self.to.isEqual(f) || self.to.isEqual(t) || (self.to.isAfter(f) && self.to.isBefore(t))

      def datesIncluded(f: LocalDate, t: LocalDate): Boolean =
        (self.from.isEqual(f) || self.from.isBefore(f)) && (self.to.isEqual(
          t
        ) || self.to
          .isAfter(t))

      def intersect(other: Ranges.Dates): Option[Ranges.Dates] =
        toIterator
          .filter(other.dateInRange(_))
          .foldLeft(Option.empty[Ranges.Dates])((acc: Option[Ranges.Dates], x: LocalDate) =>
            acc.fold(Ranges.Dates(x, x).some)(d => Ranges.Dates(d.from, x).some)
          )

      def intersectTo[F[_]](other: Ranges.Dates)(implicit a: Option ~> F): F[Ranges.Dates] =
        a(intersect(other))

      def localDate[F[_]](implicit a: Iterator ~> F): F[LocalDate] =
        a(toIterator)

      private def toIterator: Iterator[LocalDate] =
        Iterator
          .iterate(self.from)(_.plusDays(1))
          .takeWhile(x => x.isBefore(self.to) || x.isEqual(self.to))

      def toYearMonth: List[(Ints, Ints)] =
        Iterator
          .iterate(self.from.withDayOfMonth(1))(_.plusMonths(1))
          .takeWhile(x =>
            x.isBefore(self.to.withDayOfMonth(1)) || x.isEqual(
              self.to.withDayOfMonth(1)
            )
          )
          .map(x => (x.getYear.asInstanceOf[Ints], x.getMonthValue.asInstanceOf[Ints]))
          .toList
    }
  }

  final object Ints {
    /*implicit val rangeIntEnc: Encoder[Ranges.Ints] = deriveEncoder
    implicit val rangeIntDec: Decoder[Ranges.Ints] = deriveDecoder

    implicit val rangeIntO: Order[Ranges.Ints] =
      (x: Ints, y: Ints) => {
        val f: Int = x.from.compareTo(y.from)
        if (f =!= 0) {
          f
        } else {
          x.to.compareTo(y.to)
        }
      }*/

    implicit final class RangesIntsOps(private val self: Ranges.Ints) extends AnyVal {
      def contains(x: Int): Boolean = self.from <= x && self.to >= x
      def contains(x: Age): Boolean = self.from <= x.x && self.to >= x.x
    }
  }
}
