package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, LabelId}
import bookingtour.protocols.core.values.aggregates.LabelAgg
import bookingtour.protocols.core.values.api.LabelAPI
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
final case class LabelE(
    id: LabelId,
    dataId: DataId,
    lang: LangItem,
    label: String,
    stamp: Instant
)

object LabelE {
  type Id = UUID

  implicit final val itemR0: LabelE => Id = _.id

  implicit final val itemR1: LabelE => Instant = _.stamp

  implicit final val itemP0: LabelE => Int = _ => 0

  final case class Output(
      id: UUID,
      dataId: UUID,
      lang: LangItem,
      label: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => LabelE =
    _.into[LabelE]
      .withFieldComputed(_.id, x => LabelId(x.id))
      .withFieldComputed(_.dataId, x => DataId(x.dataId))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  final val toApi: LabelE => LabelAPI =
    _.into[LabelAPI].transform

  final val toAgg: LabelE => LabelAgg =
    _.into[LabelAgg].transform

  implicit final class LabelEOps(private val self: LabelE) {
    def toLabelApi: LabelAPI = toApi(self)
    def toLabelAgg: LabelAgg = toAgg(self)
  }

  @derive(order)
  final case class Create(
      dataId: UUID,
      lang: LangItem,
      label: String,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final val create: (UUID, String) => List[LabelE.Create] = (dataId, name) =>
    LangItem.values.toList.map(x =>
      LabelE
        .Create(
          dataId = dataId,
          lang = x,
          label = name
        )
    )

  @derive(order)
  final case class Update(
      id: UUID,
      parent: Option[UUID],
      solverId: Option[UUID],
      dataId: UUID,
      lang: LangItem,
      label: String,
      active: Boolean,
      deleted: Boolean
  )
}
