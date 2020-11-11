package bookingtour.protocols.api.business.business

import ContextRule.PropertyContextRule
import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.core.values.enumeration.ContextItem
import cats.Order
import cats.data.Chain
import cats.instances.int._
import cats.syntax.order._
import io.circe._
import io.circe.derivation._
import io.circe.syntax._

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class BusinessRule[-A <: ContextRule](val version: Int, val ctx: ContextItem)
    extends Product with Serializable {
  def run(params: A): Amount
  def description: String
}

object BusinessRule {
  final case class PropertyRuleV1(
      override val version: Int = 1,
      override val ctx: ContextItem = ContextItem.Accommodation
  ) extends BusinessRule[PropertyContextRule](version = version, ctx = ctx) {
    def run(params: PropertyContextRule): Amount = Amount.Euro(0.0)

    def description: String = s"property-rule-$version"
  }

  object PropertyRuleV1 {
    implicit final val propertyRuleV1Enc: Encoder[PropertyRuleV1] = deriveEncoder
    implicit final val propertyRuleV1Dec: Decoder[PropertyRuleV1] = deriveDecoder
  }

  final case class PropertyRuleV2(
      override val version: Int = 2,
      override val ctx: ContextItem = ContextItem.Accommodation
  ) extends BusinessRule[PropertyContextRule](version = version, ctx = ctx) {
    def run(params: PropertyContextRule): Amount = Amount.Euro(0.0)

    def description: String = s"property-rule-$version"
  }

  object PropertyRuleV2 {
    implicit final val propertyRuleV2Enc: Encoder[PropertyRuleV2] = deriveEncoder
    implicit final val propertyRuleV2Dec: Decoder[PropertyRuleV2] = deriveDecoder
  }

  protected final val instances: Chain[BusinessRule[PropertyContextRule]] =
    Chain(PropertyRuleV1(), PropertyRuleV2())

  implicit final val propertyRuleEnc: Encoder[BusinessRule[PropertyContextRule]] = {
    case p: PropertyRuleV1 =>
      p.asJson
    case p: PropertyRuleV2 =>
      p.asJson
  }

  implicit final val propertyRuleDec: Decoder[BusinessRule[PropertyContextRule]] =
    (c: HCursor) => {
      val probe = for {
        version <- Decoder[Int].prepare(_.downField("version"))(c)
        ctx     <- Decoder[ContextItem].prepare(_.downField("ctx"))(c)
      } yield (version, ctx)
      probe match {
        case Left(thr: DecodingFailure) =>
          Left(thr)

        case Right((v, ctx)) =>
          instances.find(x => x.version === v && x.ctx === ctx) match {
            case Some(rule) =>
              Right(rule)

            case None =>
              Left(DecodingFailure("rule not found", List.empty[CursorOp]))
          }
      }
    }

  implicit final val propertyRuleO: Order[BusinessRule[PropertyContextRule]] =
    (x: BusinessRule[PropertyContextRule], y: BusinessRule[PropertyContextRule]) =>
      CompareOps.compareFn(
        x.version.compareTo(y.version),
        x.ctx.compare(y.ctx)
      )
}
