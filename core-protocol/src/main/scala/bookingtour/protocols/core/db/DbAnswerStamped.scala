package bookingtour.protocols.core.db

import bookingtour.protocols.core.db.enumeration.AnswerStatusItem
import cats.data.NonEmptyChain
import cats.syntax.order._
import cats.{Monoid, Order}

/**
  * © Alexey Toroshchin 2019.
  */
object DbAnswerStamped {
  sealed abstract class Payload[+A, B](val status: AnswerStatusItem) extends Product with Serializable

  final case class Empty[A, B](stamp: B)                          extends Payload[A, B](AnswerStatusItem.Empty)
  final case class NonEmpty[A, B](xs: NonEmptyChain[A], stamp: B) extends Payload[A, B](AnswerStatusItem.NonEmpty)

  def empty[A, B](stamp: B): Payload[A, B] = Empty(stamp)

  trait ToMonoid {
    implicit final def dbAnswerStampedMonoid[A, B](
        emptyStamp: B
    )(implicit o: Order[B]): Monoid[Payload[A, B]] =
      new Monoid[Payload[A, B]] {
        override def empty: Payload[A, B] = Empty(emptyStamp)
        override def combine(
            x: Payload[A, B],
            y: Payload[A, B]
        ): Payload[A, B] = x match {
          case Empty(xstamp) =>
            y match {
              case Empty(ystamp) =>
                val max = o.max(xstamp, ystamp)
                Empty(max)

              case value @ NonEmpty(_, _) =>
                value
            }

          case NonEmpty(xxs, xstamp) =>
            y match {
              case Empty(ystamp) =>
                val max = o.max(xstamp, ystamp)
                NonEmpty(xxs, max)

              case NonEmpty(yxs, ystamp) =>
                val max = o.max(xstamp, ystamp)
                NonEmpty(xxs ++ yxs, max)
            }
        }
      }
  }

  final object monoid extends ToMonoid

  trait ToOrderOps {
    _: AnswerStatusItem.ToOrderOps =>
    implicit final def dbAnswerStampedO[A, B](implicit o: Order[B]): Order[Payload[A, B]] =
      (x: Payload[A, B], y: Payload[A, B]) =>
        x match {
          case NonEmpty(_, xstamp: B) =>
            y match {
              case NonEmpty(_, ystamp: B) =>
                xstamp.compare(ystamp)

              case _ =>
                x.status.compare(y.status)
            }

          case Empty(_) =>
            x.status.compare(y.status)
        }
  }

  final object order extends AnswerStatusItem.ToOrderOps with ToOrderOps
}
