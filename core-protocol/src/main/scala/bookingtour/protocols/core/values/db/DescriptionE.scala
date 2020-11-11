package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, DescriptionId}
import bookingtour.protocols.core.values.api.DescriptionAPI
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class DescriptionE(
    id: DescriptionId,
    dataId: DataId,
    lang: LangItem,
    data: String,
    stamp: Instant
)

object DescriptionE {
  type Id = DescriptionId

  implicit val itemR0: DescriptionE => Id = _.id

  implicit val itemR1: DescriptionE => Instant = _.stamp

  implicit final val itemP0: DescriptionE => Int = _ => 0

  implicit final val itemP1: DescriptionE => DataId = _.dataId

  @derive(order)
  final case class Output(
      id: UUID,
      dataId: UUID,
      lang: LangItem,
      data: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => DescriptionE =
    _.into[DescriptionE]
      .withFieldComputed(_.id, x => DescriptionId(x.id))
      .withFieldComputed(_.dataId, x => DataId(x.dataId))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  final val toApi: DescriptionE => DescriptionAPI =
    _.into[DescriptionAPI].transform

  @derive(order)
  final case class Create(
      dataId: UUID,
      lang: LangItem,
      data: String,
      solverId: Option[UUID] = None
  )

  final val create: (UUID, String) => List[Create] = (dataId, data) =>
    LangItem.values.toList.map(l => Create(dataId = dataId, lang = l, data = data))

  @derive(order)
  final case class Update(
      id: DescriptionId,
      solverId: Option[UUID],
      dataId: UUID,
      lang: LangItem,
      data: String
  )
}
