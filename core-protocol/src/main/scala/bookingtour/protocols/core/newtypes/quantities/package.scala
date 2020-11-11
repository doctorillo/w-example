package bookingtour.protocols.core.newtypes

import cats.Order
import cats.kernel.Monoid
import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
package object quantities {
  @newtype final case class SequenceNr(x: Long) {
    def next: SequenceNr = SequenceNr(x + 1L)
  }
  object SequenceNr {
    val Zero: SequenceNr                        = SequenceNr(0L)
    implicit final val itemO: Order[SequenceNr] = (x: SequenceNr, y: SequenceNr) => x.x.compareTo(y.x)
  }

  @newtype final case class Position(x: Int)
  object Position {
    val Zero: Position = Position(0)
  }

  @newtype final case class PropertyStar(x: Int)

  @newtype final case class Year(x: Int)

  @newtype final case class Month(x: Int)

  @newtype final case class Hour(x: Int)

  @newtype final case class Minute(x: Int)

  @newtype final case class Days(x: Int)
  final object Days {
    implicit val monoid: Monoid[Days] = new Monoid[Days] {
      def empty: Days = Days(0)

      def combine(x: Days, y: Days): Days = Days(x.x + y.x)
    }
  }
  @newtype final case class WeekDay(x: Int)

  @newtype final case class Nights(x: Int)
  final object Nights {
    implicit val monoid: Monoid[Nights] = new Monoid[Nights] {
      def empty: Nights = Nights(0)

      def combine(x: Nights, y: Nights): Nights = Nights(x.x + y.x)
    }
  }

  @newtype final case class Age(x: Int)
  final object Age {
    val Adult: Age = Age(25)
  }

  @newtype final case class Pax(x: Int)

  @newtype final case class PaxOnMain(x: Int)

  @newtype final case class AdultOnMain(x: Int)

  @newtype final case class ChildOnMain(x: Int)

  @newtype final case class PaxOnExtraBed(x: Int)

  @newtype final case class AdultOnExtraBed(x: Int)

  @newtype final case class ChildOnExtraBed(x: Int)

  @newtype final case class PaxWithoutBed(x: Int)

  @newtype final case class RuleModifier(x: Int)

  @newtype final case class Counter(x: Int)
  final object Counter {
    implicit val itemMonoid: Monoid[Counter] = new Monoid[Counter] {
      def empty: Counter = Counter(0)

      def combine(x: Counter, y: Counter): Counter = Counter(x.x + y.x)
    }
  }
}
