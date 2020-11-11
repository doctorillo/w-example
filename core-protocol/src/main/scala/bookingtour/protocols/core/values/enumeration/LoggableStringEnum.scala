package bookingtour.protocols.core.values.enumeration

import enumeratum.values.StringEnumEntry
import tofu.logging.Loggable

/**
  * © Alexey Toroshchin 2020.
  */
trait LoggableStringEnum[E <: StringEnumEntry] {
  final implicit val logging: Loggable[E] = Loggable.stringValue.contramap(_.value)
}
