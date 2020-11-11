package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AppContextAgg(
    id: AppContextId,
    appId: AppId,
    appIdent: AppItem,
    code: String,
    ctxType: ContextItem
)

object AppContextAgg {
  type Id = AppContextId

  implicit final val itemR0: AppContextAgg => Id = _.id

  implicit final val itemP0: AppContextAgg => AppId = _.appId
}
