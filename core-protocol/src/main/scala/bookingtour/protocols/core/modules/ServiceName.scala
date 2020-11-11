package bookingtour.protocols.core.modules

import bookingtour.protocols.core.values.enumeration.LoggableStringEnum
import derevo.cats.order
import derevo.derive
import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order, loggable)
sealed abstract class ServiceName(val value: String) extends StringEnumEntry

case object ServiceName
    extends StringEnum[ServiceName] with StringCirceEnum[ServiceName] with LoggableStringEnum[ServiceName] {
  def values: IndexedSeq[ServiceName] = findValues

  final case object Parties           extends ServiceName("parties")
  final case object Properties        extends ServiceName("properties")
  final case object PropertyPrices    extends ServiceName("property-prices")
  final case object Orders            extends ServiceName("orders")
  final case object Excursions        extends ServiceName("excursions")
  final case object InterLook         extends ServiceName("inter-look")
  final case object OperationProvider extends ServiceName("operation-provider")
  final case object OperationCustomer extends ServiceName("operation-customer")
  final case object BusinessRules     extends ServiceName("business-rules")
  final case object ApiCustomer       extends ServiceName("api-customer")
}
