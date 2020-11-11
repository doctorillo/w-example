package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookBoardingCategoryId, LookBoardingId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BoardingEP(
    id: LookBoardingId,
    code: String,
    name: String,
    categoryId: Option[LookBoardingCategoryId]
)

object BoardingEP {
  type Id = LookBoardingId

  implicit final val itemR: BoardingEP => Id = _.id

  implicit final val itemP: BoardingEP => Int = _ => 0

  final case class Output(
      id: Int,
      code: Option[String],
      name: Option[String],
      category: Option[Int]
  )

  implicit final val outputTransform: Output => BoardingEP = _.into[BoardingEP]
    .withFieldComputed(_.id, x => LookBoardingId(x.id))
    .withFieldComputed(_.code, _.code.getOrElse("NO CODE"))
    .withFieldComputed(_.name, _.name.getOrElse("NO NAME"))
    .withFieldComputed(_.categoryId, _.category.map(LookBoardingCategoryId(_)))
    .transform
}
