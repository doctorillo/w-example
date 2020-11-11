package bookingtour.protocols.core.types

import bookingtour.protocols.core.db.DbAnswer
import bookingtour.protocols.core.db.DbAnswer.Payload
import cats.data._
import cats.{~>, Id}

/**
  * © Alexey Toroshchin 2019.
  */
object FunctionKCore {
  final object instances {
    implicit val iteratorListFK: Iterator ~> List = new ~>[Iterator, List] {
      override def apply[A](fa: Iterator[A]): List[A] = fa.toList
    }
    implicit val optionListFK: Option ~> List = new ~>[Option, List] {
      override def apply[A](fa: Option[A]): List[A] = fa.toList
    }
    /*implicit val idListFK: Id ~> List = new ~>[Id, List] {
      override def apply[A](fa: Id[A]): List[A] = List(fa)
    }
    implicit val idOptionFK: Id ~> Option = new ~>[Id, Option] {
      override def apply[A](fa: Id[A]): Option[A] = Some(fa)
    }*/
    implicit val listDbAnswerFK: List ~> Payload = new ~>[List, DbAnswer.Payload] {
      override def apply[A](fa: List[A]): DbAnswer.Payload[A] =
        if (fa.isEmpty) {
          DbAnswer.Empty
        } else {
          DbAnswer.NonEmpty(fa)
        }
    }

    def listStampedFK[A, B](default: B)(
        implicit r: Reader[NonEmptyChain[A], B]
    ): List[A] => (Chain[A], Id[B]) = (list: List[A]) => {
      if (list.isEmpty) {
        (Chain.empty, default)
      } else {
        val c = Chain.fromSeq(list)
        (c, r(NonEmptyChain.fromChainUnsafe(c)))
      }
    }
    implicit final class FunctionKOps[F[_], A](private val self: F[A]) extends AnyVal {
      def liftFK[Z[_]](implicit a: F ~> Z): Z[A] = a(self)
    }
  }
}
