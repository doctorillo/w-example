package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import derevo.cats.order
import derevo.derive
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class RoleItem(val value: Int, val context: ContextItem) extends IntEnumEntry

case object RoleItem extends IntEnum[RoleItem] with IntCirceEnum[RoleItem] with LoggableIntEnum[RoleItem] {
  override def values: immutable.IndexedSeq[RoleItem] = findValues

  final def fromRoleString(name: String): RoleItem = name match {
    case "root" =>
      Root

    case "accommodation_admin" =>
      AccommodationAdmin

    case "acc admin" =>
      AccommodationAdmin

    case "accommodation_user" =>
      AccommodationUser

    case "acc viewer" =>
      AccommodationUser

    case "transport_admin" =>
      TransportAdmin

    case "transport_user" =>
      TransportAdmin

    case "trans admin" =>
      TransportAdmin

    case "trans viewer" =>
      TransportUser

    case "excursion_admin" =>
      ExcursionAdmin

    case "exc admin" =>
      ExcursionAdmin

    case "excursion_user" =>
      ExcursionUser

    case "exc viewer" =>
      ExcursionUser

    case "guide" =>
      Guide

    case "driver" =>
      Driver

    case "delegate" =>
      Delegate

    case "accountant" =>
      Accountant

    case _ =>
      Undefined
  }

  case object Undefined extends RoleItem(value = -1, context = ContextItem.All)

  case object Root extends RoleItem(value = 0, context = ContextItem.All)

  case object AccommodationAdmin extends RoleItem(value = 1, context = ContextItem.Accommodation)

  case object AccommodationUser extends RoleItem(value = 10, context = ContextItem.Accommodation)

  case object ExcursionAdmin extends RoleItem(value = 2, context = ContextItem.Excursion)

  case object ExcursionUser extends RoleItem(value = 20, context = ContextItem.Excursion)

  case object Guide extends RoleItem(value = 21, context = ContextItem.Excursion)

  case object TransportAdmin extends RoleItem(value = 3, context = ContextItem.Transport)

  case object TransportUser extends RoleItem(value = 30, context = ContextItem.Transport)

  case object Driver extends RoleItem(value = 31, context = ContextItem.Transport)

  case object Delegate extends RoleItem(value = 32, context = ContextItem.Transport)

  case object Accountant extends RoleItem(value = 5, context = ContextItem.All)
}
