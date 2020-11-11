package bookingtour.protocols.core.db

import bookingtour.protocols.core.db.enumeration.AnswerStatusItem
import bookingtour.protocols.core.db.enumeration.AnswerStatusItem
import cats.{~>, Applicative, Monad, Monoid, Order}

/**
  * © Alexey Toroshchin 2019.
  */
object DbAnswer {
  sealed abstract class Payload[+A](val status: AnswerStatusItem) extends Product with Serializable

  final case object Empty                   extends Payload(AnswerStatusItem.Empty)
  final case class NonEmpty[A](xs: List[A]) extends Payload(AnswerStatusItem.NonEmpty)

  trait ToOrderOps {
    _: AnswerStatusItem.ToOrderOps =>
    implicit final def dbAnswerO[A](implicit o: Order[A]): Order[Payload[A]] =
      (x: Payload[A], y: Payload[A]) => {
        x match {
          case Empty =>
            y match {
              case Empty =>
                0
              case NonEmpty(_) =>
                1
            }
          case nex: NonEmpty[_] =>
            y match {
              case Empty =>
                -1
              case yex: NonEmpty[_] =>
                val _p = nex.xs.diff(yex.xs)
                if (_p.isEmpty) {
                  0
                } else {
                  val r = nex.xs.size - yex.xs.size
                  if (r <= 0) {
                    -1
                  } else {
                    1
                  }
                }
            }
        }
      }
  }

  final object order extends AnswerStatusItem.ToOrderOps with ToOrderOps

  trait ToMonoid {
    implicit final def dbAnswerMonoid[A]: Monoid[Payload[A]] =
      new Monoid[Payload[A]] {
        override def empty: Payload[A] = Empty
        override def combine(
            x: Payload[A],
            y: Payload[A]
        ): Payload[A] = x match {
          case Empty =>
            y

          case value @ NonEmpty(xs) =>
            y match {
              case Empty =>
                value

              case NonEmpty(yxs) =>
                NonEmpty(xs ++ yxs)
            }
        }
      }
  }

  final object monoid extends ToMonoid

  trait ToApplicativeOps {
    implicit final val dbAnswerApplicative: Applicative[Payload] = new Applicative[Payload] {
      override def pure[A](x: A): Payload[A] = NonEmpty(List(x))
      override def ap[A, B](ff: Payload[A => B])(fa: Payload[A]): Payload[B] = ff match {
        case Empty =>
          Empty
        case NonEmpty(xs) =>
          fa match {
            case Empty =>
              Empty

            case NonEmpty(zs) =>
              val xst = xs.asInstanceOf[List[A => B]]
              val rxs = zs.asInstanceOf[List[A]].flatMap(z => xst.map(x => x(z)))
              NonEmpty(rxs)
          }
      }
    }
  }

  final object applicative extends ToApplicativeOps

  trait ToArrowOps {
    final def arrow[C[_], T, T2](
        list: List[C[T]]
    )(implicit m: Monad[C], fk: C ~> List, f: T => T2): DbAnswer.Payload[T2] = {
      val xs = list.flatMap(x => fk(m.map(x)(f)))
      if (xs.isEmpty) {
        DbAnswer.Empty
      } else {
        DbAnswer.NonEmpty(xs)
      }
    }
  }

  final object arrow extends ToArrowOps
}
