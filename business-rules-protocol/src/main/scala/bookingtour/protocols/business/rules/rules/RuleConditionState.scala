package bookingtour.protocols.business.rules.rules

import bookingtour.protocols.business.rules.enumeration.ConditionCategoryItem
import bookingtour.protocols.business.rules.enumeration.ConditionCategoryItem
import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.instances.list._
import cats.syntax.functor._
import cats.syntax.order._
import io.circe.derivation._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class RuleConditionState[T](val category: ConditionCategoryItem) extends Product with Serializable

object RuleConditionState {
  final case class Undefined[T](override val category: ConditionCategoryItem) extends RuleConditionState[T](category)
  final object Undefined {
    implicit def circeEnc[T: Encoder]: Encoder[Undefined[T]] = deriveEncoder
    implicit def circeDec[T: Decoder]: Decoder[Undefined[T]] = deriveDecoder
    implicit def catsO[T: Order]: Order[Undefined[T]] =
      (x: Undefined[T], y: Undefined[T]) => x.category.compare(y.category)
  }

  final case class Custom[T](override val category: ConditionCategoryItem, data: List[T])
      extends RuleConditionState[T](category)
  final object Custom {
    implicit def circeEnc[T: Encoder]: Encoder.AsObject[Custom[T]] = deriveEncoder
    implicit def circeDec[T: Decoder]: Decoder[Custom[T]]          = deriveDecoder
    implicit def catsO[T: Order]: Order[Custom[T]] =
      (x: Custom[T], y: Custom[T]) => CompareOps.compareFn(x.category.compare(y.category), x.data.compare(y.data))
  }

  final case class All[T](override val category: ConditionCategoryItem) extends RuleConditionState[T](category)
  final object All {
    implicit def circeEnc[T: Encoder]: Encoder[All[T]] = deriveEncoder
    implicit def circeDec[T: Decoder]: Decoder[All[T]] = deriveDecoder
    implicit def catsO[T: Order]: Order[All[T]] =
      (x: All[T], y: All[T]) => x.category.compare(y.category)
  }

  implicit final def circeEnc[T: Encoder]: Encoder[RuleConditionState[T]] =
    Encoder.instance {
      case msg: Undefined[T] =>
        msg.asJson
      case msg @ All(_) =>
        msg.asJson
      case msg: Custom[T] =>
        msg.asJson
    }

  implicit final def circeDecoder[T: Decoder]: Decoder[RuleConditionState[T]] =
    List[Decoder[RuleConditionState[T]]](
      Decoder[Undefined[T]].widen,
      Decoder[Custom[T]].widen,
      Decoder[All[T]].widen
    ).reduceLeft(_.or(_))

  implicit final def catsO[T: Order]: Order[RuleConditionState[T]] =
    (x: RuleConditionState[T], y: RuleConditionState[T]) =>
      (x, y) match {
        case (Custom(keyX, dataX), Custom(keyY, dataY)) =>
          if (keyX === keyY) {
            dataX.compare(dataY)
          } else {
            keyX.compare(keyY)
          }
        case (x, y) =>
          x.category.compare(y.category)
      }
}
