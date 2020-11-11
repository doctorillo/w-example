package bookingtour.protocols

import java.time.format.{DateTimeFormatter, FormatStyle}
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZoneOffset}
import java.util.UUID

import akka.actor.ActorRef
import cats.data.NonEmptyList
import cats.{Monoid, Order, Show}
import eu.timepit.refined.api.{RefType, Validate}
import io.circe.{Decoder, DecodingFailure, Encoder, KeyDecoder, KeyEncoder}
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import tofu.logging.{LogParamValue, LogRenderer, Loggable, SingleValueLoggable, StrValue}

import scala.collection.SortedMap

/**
  * © Alexey Toroshchin 2019.
  */
package object core {
  implicit final val actorRefO: Order[ActorRef]                     = (x: ActorRef, y: ActorRef) => x.compareTo(y)
  implicit final val localDateO: Order[LocalDate]                   = (x: LocalDate, y: LocalDate) => x.compareTo(y)
  implicit final val localDateOrdering: Ordering[LocalDate]         = localDateO.toOrdering
  implicit final val localDateTimeO: Order[LocalDateTime]           = (x: LocalDateTime, y: LocalDateTime) => x.compareTo(y)
  implicit final val localDateTimeOrdering: Ordering[LocalDateTime] = localDateTimeO.toOrdering
  implicit final val localTimeO: Order[LocalTime]                   = (x: LocalTime, y: LocalTime) => x.compareTo(y)
  implicit final val localTimeOrdering: Ordering[LocalTime]         = localTimeO.toOrdering
  implicit final val instantO: Order[Instant]                       = (x: Instant, y: Instant) => x.compareTo(y)
  implicit final val instantOrdering: Ordering[Instant]             = instantO.toOrdering

  // implicit final def orderingItem[A: Order]: Ordering[A] = Order[A].toOrdering

  final implicit val uuidLoggable: Loggable[UUID] = new SingleValueLoggable[UUID] {
    def logValue(a: UUID): LogParamValue = StrValue(a.toString)
    override def putField[I, V, R, M](a: UUID, name: String, input: I)(implicit receiver: LogRenderer[I, V, R, M]): R =
      receiver.addString(name, a.toString, input)
  }
  private val instantFormatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneOffset.UTC)
  final implicit val instantShow: Show[Instant] = Show[Instant](x => instantFormatter.format(x))

  final implicit val localDateShow: Show[LocalDate] = Show[LocalDate](x => instantFormatter.format(x))

  implicit final val localDateLoggable: Loggable[LocalDate] = Loggable.localDateTimeLoggable.contramap(_.atStartOfDay())

  implicit final def coercibleShow[A: Coercible[B, *], B: Show]: Show[A] =
    Show[B].repr.asInstanceOf[Show[A]]

  implicit final def coercibleLoggable[A: Coercible[B, *], B: Loggable]: Loggable[A] =
    Loggable[B].contramap(_.repr.asInstanceOf[B])

  implicit final def coercibleDecoder[
      A: Coercible[B, *],
      B: Decoder
  ]: Decoder[A] = Decoder[B].map(_.coerce[A])

  implicit final def coercibleEncoder[
      A: Coercible[B, *],
      B: Encoder
  ]: Encoder[A] = Encoder[B].contramap(_.repr.asInstanceOf[B])

  implicit final def coercibleOrder[A: Coercible[B, *], B: Order]: Order[A] = Order[B].asInstanceOf[Order[A]]

  implicit final def refinedDecoder[T, P, F[_, _]](
      implicit
      underlying: Decoder[T],
      validate: Validate[T, P],
      refType: RefType[F]
  ): Decoder[F[T, P]] =
    Decoder.instance { c =>
      underlying(c) match {
        case Right(t0) =>
          refType.refine(t0) match {
            case Left(err)    => Left(DecodingFailure(err, c.history))
            case r @ Right(_) => r.asInstanceOf[Decoder.Result[F[T, P]]]
          }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[F[T, P]]]
      }
    }

  implicit final def refinedEncoder[T, P, F[_, _]](
      implicit
      underlying: Encoder[T],
      refType: RefType[F]
  ): Encoder[F[T, P]] =
    underlying.contramap(refType.unwrap)

  implicit final def refinedKeyDecoder[T, P, F[_, _]](
      implicit
      underlying: KeyDecoder[T],
      validate: Validate[T, P],
      refType: RefType[F]
  ): KeyDecoder[F[T, P]] =
    KeyDecoder.instance { str =>
      underlying(str).flatMap { t0 =>
        refType.refine(t0) match {
          case Left(_)  => None
          case Right(t) => Some(t)
        }
      }
    }

  implicit final def refinedKeyEncoder[T, P, F[_, _]](
      implicit
      underlying: KeyEncoder[T],
      refType: RefType[F]
  ): KeyEncoder[F[T, P]] =
    underlying.contramap(refType.unwrap)

  implicit final def sortedMapNelMonoid[K: Order, V: Order]: Monoid[SortedMap[K, NonEmptyList[V]]] = {
    implicit val o1: Ordering[K] = Order[K].toOrdering
    implicit val o2: Ordering[V] = Order[V].toOrdering

    new Monoid[SortedMap[K, NonEmptyList[V]]] {
      def empty: SortedMap[K, NonEmptyList[V]] = SortedMap.empty[K, NonEmptyList[V]]

      def combine(x: SortedMap[K, NonEmptyList[V]], y: SortedMap[K, NonEmptyList[V]]): SortedMap[K, NonEmptyList[V]] = {
        val xKeys = x.keys.toList
        val yKeys = y.keys.toList
        val keys  = xKeys ++ yKeys
        val values = for {
          k       <- keys
          xValues <- x.get(k).map(_.toList).toList
          yValues <- y.get(k).map(_.toList).toList
          xy      = (xValues ++ yValues).sorted
          if xy.nonEmpty
        } yield (k, NonEmptyList.fromListUnsafe(xy))
        SortedMap.from[K, NonEmptyList[V]](values)
      }
    }
  }
}
