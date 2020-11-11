package bookingtour.protocols.core.values.enumeration

import enumeratum.values.IntEnumEntry
import tofu.logging.Loggable

/**
  * © Alexey Toroshchin 2020.
  */
trait LoggableIntEnum[E <: IntEnumEntry] {
  final implicit val logging: Loggable[E] = Loggable.intLoggable.contramap(_.value)
}
