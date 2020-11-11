package bookingtour.protocols.parties.alg

import scala.annotation.tailrec

import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId, ProviderId}
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId, ProviderId}

/**
  * © Alexey Toroshchin 2019.
  */
sealed trait RelationOrg {
  val partyId: PartyId
  val partyName: String
  val context: ContextItem
}

object RelationOrg {

  /**
    * Orphan party
    * */
  final case class OrphanOrg(partyId: PartyId, partyName: String, context: ContextItem) extends RelationOrg

  /**
    * init element of RelationOrg algebra
    * @param relations - upper-level customer groups
    * */
  final case class InitCustomerOrg(
      partyId: PartyId,
      partyName: String,
      context: ContextItem,
      relations: List[RelationOrg]
  ) extends RelationOrg {
    def askTerminate(): List[TerminateCustomerOrg] = {
      @tailrec
      def go(rxs: List[TerminateCustomerOrg], xs: List[RelationOrg]): List[TerminateCustomerOrg] =
        xs match {
          case Nil =>
            rxs
          case head :: tail =>
            head match {
              case x: TerminateCustomerOrg =>
                go(rxs :+ x, tail)

              case x: CustomerOrg =>
                go(rxs, tail ++ x.relations)

              case _ =>
                go(rxs, tail)
            }

        }
      go(List.empty, relations)
    }
  }

  /**
    * terminate element of RelationOrg algebra
    * */
  final case class TerminateCustomerOrg(
      groupId: CustomerGroupId,
      partyId: PartyId,
      partyName: String,
      context: ContextItem,
      provider: ProviderId
  ) extends RelationOrg

  /**
    * @param relations - upper-level customer groups
    * */
  final case class CustomerOrg(
      groupId: CustomerGroupId,
      partyId: PartyId,
      partyName: String,
      context: ContextItem,
      provider: ProviderId,
      relations: List[RelationOrg]
  ) extends RelationOrg {
    def toTerminate: TerminateCustomerOrg =
      TerminateCustomerOrg(
        groupId = groupId,
        partyId = partyId,
        partyName = partyName,
        context = context,
        provider = provider
      )
  }
}
