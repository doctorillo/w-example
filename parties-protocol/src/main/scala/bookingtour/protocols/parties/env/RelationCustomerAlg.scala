package bookingtour.protocols.parties.env

import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.parties.alg.RelationOrg.{InitCustomerOrg, TerminateCustomerOrg}
import bookingtour.protocols.parties.api.PartyValue
import bookingtour.protocols.parties.api.PartyValue
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.parties.newTypes.PartyId
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait RelationCustomerAlg extends Serializable {
  val relationCustomerAlg: RelationCustomerAlg.Service[Any]
}

object RelationCustomerAlg {
  trait Service[R] {
    def makeInit(id: PartyId, context: ContextItem): URIO[R, InitCustomerOrg]

    def fetchTerminate(
        org: InitCustomerOrg
    ): URIO[R, List[TerminateCustomerOrg]]

    def fetchCustomers(id: PartyId, context: ContextItem): URIO[R, List[PartyValue]]
  }

  final object > extends Service[RelationCustomerAlg] {
    def makeInit(id: PartyId, context: ContextItem): URIO[RelationCustomerAlg, InitCustomerOrg] =
      ZIO.accessM(_.relationCustomerAlg.makeInit(id, context))

    def fetchTerminate(
        org: InitCustomerOrg
    ): URIO[RelationCustomerAlg, List[TerminateCustomerOrg]] =
      ZIO.accessM(_.relationCustomerAlg.fetchTerminate(org))

    def fetchCustomers(
        id: PartyId,
        context: ContextItem
    ): URIO[RelationCustomerAlg, List[PartyValue]] =
      ZIO.accessM(_.relationCustomerAlg.fetchCustomers(id, context))
  }
}
