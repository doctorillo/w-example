package bookingtour.protocols.parties.env.live

import scala.collection.immutable.Map

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem}
import bookingtour.protocols.parties.agg.basic.ProviderDataAgg
import bookingtour.protocols.parties.alg.RelationOrg
import bookingtour.protocols.parties.alg.RelationOrg._
import bookingtour.protocols.parties.alg.RelationOrg.{CustomerOrg, InitCustomerOrg, TerminateCustomerOrg}
import bookingtour.protocols.parties.api.PartyValue
import bookingtour.protocols.parties.env.RelationCustomerAlg
import bookingtour.protocols.parties.env.live.LiveRelationCustomerAlg.{PROVIDERS, RelationSource}
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId, ProviderId, SupplierGroupId}
import cats.instances.all._
import cats.syntax.order._
import zio.{UIO, URIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveRelationCustomerAlg private (app: AppItem)(
    implicit providerAlg: ConsumerAlg.Aux[Any, Int, ProviderDataAgg]
) extends RelationCustomerAlg {

  override val relationCustomerAlg: RelationCustomerAlg.Service[Any] =
    new RelationCustomerAlg.Service[Any] {
      private final def source(
          app: AppItem,
          context: ContextItem
      ): URIO[Any, PROVIDERS] =
        providerAlg
          .byValue(condition = x => x.ctx.appIdent === app && x.ctx.ctxType === context)
          .map(_.map(LiveRelationCustomerAlg.toSource(_)))
          .catchAll(_ => ZIO.succeed(List.empty[RelationSource]))

      private final def customerOrg(org: CustomerOrg, state: PROVIDERS): UIO[RelationOrg] = {
        val orgs = for {
          (providerId, partyId, name, groupId) <- state.flatMap(x =>
                                                   x.customers
                                                     .filter(x => x._2.exists(_.id === org.partyId))
                                                     .map(z =>
                                                       (x.providerId, x.partyId, x.partyName, CustomerGroupId(z._1.x))
                                                     )
                                                 )
        } yield CustomerOrg(
          groupId = groupId,
          partyId = partyId,
          partyName = name,
          context = org.context,
          provider = providerId,
          relations = List.empty
        )
        if (orgs.isEmpty) {
          ZIO.effectTotal(org.toTerminate)
        } else {
          ZIO
            .foreach(orgs)(x => customerOrg(x, state))
            .map(xs => org.copy(relations = xs))
        }
      }

      private final def fillRelations(
          org: InitCustomerOrg,
          state: PROVIDERS
      ): UIO[InitCustomerOrg] = {
        val orgs = for {
          (providerId, partyId, name, groupId) <- state.flatMap(x =>
                                                   x.customers
                                                     .filter(x => x._2.exists(_.id === org.partyId))
                                                     .map(z =>
                                                       (x.providerId, x.partyId, x.partyName, CustomerGroupId(z._1.x))
                                                     )
                                                 )
        } yield CustomerOrg(
          groupId = groupId,
          partyId = partyId,
          partyName = name,
          context = org.context,
          provider = providerId,
          relations = List.empty
        )
        if (orgs.isEmpty) {
          ZIO.effectTotal(
            org.copy(relations = List(OrphanOrg(org.partyId, org.partyName, org.context)))
          )
        } else {
          ZIO
            .foreach(orgs)(x => customerOrg(x, state))
            .map(xs => org.copy(relations = xs))
        }
      }

      final def matchTerminate(
          org: RelationOrg
      ): URIO[Any, List[TerminateCustomerOrg]] = org match {
        case OrphanOrg(_, _, _) =>
          ZIO.succeed(List.empty)

        case msg: TerminateCustomerOrg =>
          ZIO.succeed(List(msg))

        case InitCustomerOrg(_, _, _, relations) =>
          ZIO.collectAll(relations.map(x => matchTerminate(x))).map(_.flatten)

        case CustomerOrg(_, _, _, _, _, relations) =>
          ZIO.collectAll(relations.map(x => matchTerminate(x))).map(_.flatten)
      }

      def makeInit(
          id: PartyId,
          context: ContextItem
      ): URIO[Any, InitCustomerOrg] =
        for {
          a <- source(app, context)
          b <- ZIO.effectTotal(a.flatMap(_.customers.flatMap(_._2.filter(_.id === id))))
          c <- fillRelations(InitCustomerOrg(id, b.head.name, context, List.empty), a)
        } yield c

      def fetchTerminate(org: InitCustomerOrg): URIO[Any, List[TerminateCustomerOrg]] =
        matchTerminate(org)

      def fetchCustomers(id: PartyId, context: ContextItem): URIO[Any, List[PartyValue]] =
        source(app, context)
          .map(_.find(x => x.ctx === context && x.partyId === id).toList.flatMap(_.customers.flatMap(_._2).toList))
    }
}

object LiveRelationCustomerAlg {
  import io.scalaland.chimney.dsl._

  final case class RelationSource(
      providerId: ProviderId,
      ctx: ContextItem,
      partyId: PartyId,
      partyName: String,
      suppliers: Map[SupplierGroupId, List[PartyId]],
      customers: Map[CustomerGroupId, List[PartyValue]]
  )

  final type PROVIDERS = List[RelationSource]

  final val toSource: ProviderDataAgg => RelationSource = _.into[RelationSource]
    .withFieldComputed(_.providerId, _.id)
    .withFieldComputed(_.ctx, _.ctx.ctxType)
    .withFieldComputed(_.partyId, _.company.party.id)
    .withFieldComputed(_.partyName, _.company.name)
    .withFieldComputed(_.suppliers, _.suppliers.map(z => (z.id, z.members)).toMap)
    .withFieldComputed(
      _.customers,
      _.customers.map(z => (z.id, z.members.map(_.toPartyValue))).toMap
    )
    .transform

  final def apply(appItem: AppItem = AppItem.Partner)(
      implicit providerAlg: ConsumerAlg.Aux[Any, Int, ProviderDataAgg]
  ): RelationCustomerAlg = new LiveRelationCustomerAlg(app = appItem)
}
