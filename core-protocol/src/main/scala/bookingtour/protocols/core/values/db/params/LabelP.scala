package bookingtour.protocols.core.values.db.params

import bookingtour.protocols.core.values.enumeration.LangItem
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order)
final case class LabelP(lang: LangItem, label: String)

object LabelP {
  type Id = LabelP

  implicit final val labelPR: LabelP => Id = x => x
}
