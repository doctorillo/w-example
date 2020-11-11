package bookingtour.ms.parties.modules

import java.time.Instant
import java.util.UUID

import bookingtour.core.actors.kafka.enricher.KafkaEnricherActor
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.core.actors.modules.{AkkaModule, KafkaTopicModule}
import bookingtour.core.actors.primitives.channel.basic.BasicChannelActor
import bookingtour.core.doobie.basic.labels.LabelOps
import bookingtour.core.doobie.basic.syncs.SyncOps
import bookingtour.core.doobie.basic.syncs.SyncOps
import bookingtour.data.parties.sql.contexts.app.AppPartnerOps
import bookingtour.data.parties.sql.contexts.appcontexts.AppContextOps
import bookingtour.data.parties.sql.contexts.langs.AppLangOps
import bookingtour.data.parties.sql.geo.cities.CityOps
import bookingtour.data.parties.sql.geo.countries.CountryOps
import bookingtour.data.parties.sql.geo.districts.DistrictOps
import bookingtour.data.parties.sql.geo.points.PickupPointOps
import bookingtour.data.parties.sql.geo.regions.RegionOps
import bookingtour.data.parties.sql.parties.address.AddressOps
import bookingtour.data.parties.sql.parties.companies.CompanyOps
import bookingtour.data.parties.sql.parties.customergroups.CustomerGroupOps
import bookingtour.data.parties.sql.parties.customermembers.CustomerGroupMemberOps
import bookingtour.data.parties.sql.parties.parties.PartyOps
import bookingtour.data.parties.sql.parties.persons.PersonOps
import bookingtour.data.parties.sql.parties.providers.ProviderOps
import bookingtour.data.parties.sql.parties.suppliergroups.SupplierGroupOps
import bookingtour.data.parties.sql.parties.suppliermembers.SupplierGroupMemberOps
import bookingtour.data.parties.sql.solvers.SolverOps
import bookingtour.data.parties.sql.users.UserOps
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core._
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.core.values.db.{LabelE, SyncE}
import bookingtour.protocols.parties.Tables
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.parties.values._
import bookingtour.protocols.parties.values.{
  AddressPREP,
  AppContextPREP,
  AppPREP,
  CityDistrictPREP,
  CityPREP,
  CompanyPREP,
  CountryPREP,
  CustomerGroupMemberPREP,
  CustomerGroupPREP,
  PartyPREP,
  PersonPREP,
  PickupPointPREP,
  ProviderPREP,
  RegionPREP,
  SolverPREP,
  SupplierGroupMemberPREP,
  SupplierGroupPREP,
  UserPREP
}
import cats.data.NonEmptyList
import cats.instances.all._
import doobie.hikari.HikariTransactor
import zio.{Managed, Task}

/**
  * © Alexey Toroshchin 2019.
  */
final class DataOps private (
    implicit bm: BaseModule,
    rm: RuntimeModule,
    tx: Managed[Throwable, HikariTransactor[Task]],
    am: AkkaModule,
    ktm: KafkaTopicModule,
    ke: KafkaEdge.KafkaEdgeWrapper
) {
  import am._
  import bm._
  import ktm._
  import rm._

  //implicit private val a: UUID => UUID = x => x
  implicit private def maxStamp[A](xs: List[A])(implicit a: A => Instant): Instant =
    NonEmptyList.fromList(xs).map(xs => xs.map(a).toList.max).getOrElse(systemStart)

  val appEA: ActorProducer[AppPREP, AppPREP.Id] =
    KafkaEnricherActor.make[AppPREP, UUID, AppPREP.Id, Instant](
      uniqueTag = "apps-pre",
      table = Tables.apps,
      topic = topics.partiesWatchTopic,
      dataOps = AppPartnerOps(),
      channelFactory = BasicChannelActor
        .makeChannel[AppPREP, AppPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val appLangEA: ActorProducer[AppLangPREP, AppLangPREP.Id] =
    KafkaEnricherActor.make[AppLangPREP, UUID, AppLangPREP.Id, Instant](
      uniqueTag = "app-langs-pre",
      table = Tables.app_langs,
      topic = topics.partiesWatchTopic,
      dataOps = AppLangOps(),
      channelFactory = BasicChannelActor
        .makeChannel[AppLangPREP, AppLangPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val appContextEA: ActorProducer[AppContextPREP, AppContextPREP.Id] =
    KafkaEnricherActor.make[AppContextPREP, UUID, AppContextPREP.Id, Instant](
      uniqueTag = "app-contexts-pre",
      table = Tables.app_contexts,
      topic = topics.partiesWatchTopic,
      dataOps = AppContextOps(),
      channelFactory = BasicChannelActor
        .makeChannel[AppContextPREP, AppContextPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val partyEA: ActorProducer[PartyPREP, PartyId] =
    KafkaEnricherActor.make[PartyPREP, UUID, PartyId, Instant](
      uniqueTag = "parties-pre",
      table = Tables.parties,
      topic = topics.partiesWatchTopic,
      dataOps = PartyOps(),
      channelFactory = BasicChannelActor
        .makeChannel[PartyPREP, PartyPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val partySyncsEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "party-syncs-pre",
      table = Tables.party_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.party_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val companiesEA: ActorProducer[CompanyPREP, CompanyPREP.Id] =
    KafkaEnricherActor.make[CompanyPREP, UUID, CompanyPREP.Id, Instant](
      uniqueTag = "companies-pre",
      table = Tables.companies,
      topic = topics.partiesWatchTopic,
      dataOps = CompanyOps(),
      channelFactory = BasicChannelActor
        .makeChannel[CompanyPREP, CompanyPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val personsEA: ActorProducer[PersonPREP, PersonPREP.Id] =
    KafkaEnricherActor.make[PersonPREP, UUID, PersonPREP.Id, Instant](
      uniqueTag = "persons-pre",
      table = Tables.persons,
      topic = topics.partiesWatchTopic,
      dataOps = PersonOps(),
      channelFactory = BasicChannelActor
        .makeChannel[PersonPREP, PersonPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val providerEA: ActorProducer[ProviderPREP, ProviderPREP.Id] =
    KafkaEnricherActor.make[ProviderPREP, UUID, ProviderPREP.Id, Instant](
      uniqueTag = "provider-pre",
      table = Tables.providers,
      topic = topics.partiesWatchTopic,
      dataOps = ProviderOps(),
      channelFactory = BasicChannelActor
        .makeChannel[ProviderPREP, ProviderPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val providerSupplierGroupEA: ActorProducer[SupplierGroupPREP, SupplierGroupPREP.Id] =
    KafkaEnricherActor.make[SupplierGroupPREP, UUID, SupplierGroupPREP.Id, Instant](
      uniqueTag = "supplier-group-pre",
      table = Tables.provider_supplier_groups,
      topic = topics.partiesWatchTopic,
      dataOps = SupplierGroupOps(),
      channelFactory = BasicChannelActor
        .makeChannel[SupplierGroupPREP, SupplierGroupPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val providerSupplierGroupMemberEA: ActorProducer[
    SupplierGroupMemberPREP,
    SupplierGroupMemberPREP.Id
  ] =
    KafkaEnricherActor.make[SupplierGroupMemberPREP, UUID, SupplierGroupMemberPREP.Id, Instant](
      uniqueTag = "supplier-group-member-pre",
      table = Tables.provider_supplier_members,
      topic = topics.partiesWatchTopic,
      dataOps = SupplierGroupMemberOps(),
      channelFactory = BasicChannelActor
        .makeChannel[SupplierGroupMemberPREP, SupplierGroupMemberPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val providerCustomerGroupEA: ActorProducer[CustomerGroupPREP, CustomerGroupPREP.Id] =
    KafkaEnricherActor.make[CustomerGroupPREP, UUID, CustomerGroupPREP.Id, Instant](
      uniqueTag = "customer-group-pre",
      table = Tables.provider_customer_groups,
      topic = topics.partiesWatchTopic,
      dataOps = CustomerGroupOps(),
      channelFactory = BasicChannelActor
        .makeChannel[CustomerGroupPREP, CustomerGroupPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val providerCustomerGroupSyncEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "provider-customer-group-sync-pre",
      table = Tables.provider_customer_group_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.provider_customer_group_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val providerCustomerGroupMemberEA: ActorProducer[
    CustomerGroupMemberPREP,
    CustomerGroupMemberPREP.Id
  ] =
    KafkaEnricherActor.make[CustomerGroupMemberPREP, UUID, CustomerGroupMemberPREP.Id, Instant](
      uniqueTag = "provider-customer-group-member-pre",
      table = Tables.provider_customer_members,
      topic = topics.partiesWatchTopic,
      dataOps = CustomerGroupMemberOps(),
      channelFactory = BasicChannelActor
        .makeChannel[CustomerGroupMemberPREP, CustomerGroupMemberPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val solverEA: ActorProducer[SolverPREP, SolverPREP.Id] =
    KafkaEnricherActor.make[SolverPREP, UUID, SolverPREP.Id, Instant](
      uniqueTag = "solver-pre",
      table = Tables.solvers,
      topic = topics.partiesWatchTopic,
      dataOps = SolverOps(),
      channelFactory = BasicChannelActor
        .makeChannel[SolverPREP, SolverPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val solverSyncEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "solver-sync-pre",
      table = Tables.solver_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.solver_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart
    )

  val userEA: ActorProducer[UserPREP, UserPREP.Id] =
    KafkaEnricherActor.make[UserPREP, UUID, UserPREP.Id, Instant](
      uniqueTag = "user-pre",
      table = Tables.users,
      topic = topics.partiesWatchTopic,
      dataOps = UserOps(),
      channelFactory = BasicChannelActor
        .makeChannel[UserPREP, UserPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val countryEA: ActorProducer[CountryPREP, CountryPREP.Id] =
    KafkaEnricherActor.make[CountryPREP, UUID, CountryPREP.Id, Instant](
      uniqueTag = "country-pre",
      table = Tables.countries,
      topic = topics.partiesWatchTopic,
      dataOps = CountryOps(),
      channelFactory = BasicChannelActor
        .makeChannel[CountryPREP, CountryPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val countryLabelEA: ActorProducer[LabelE, LabelE.Id] =
    KafkaEnricherActor.make[LabelE, UUID, LabelE.Id, Instant](
      uniqueTag = "country-label-pre",
      table = Tables.country_labels,
      topic = topics.partiesWatchTopic,
      dataOps = LabelOps(Tables.country_labels),
      channelFactory = BasicChannelActor
        .makeChannel[LabelE, LabelE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val countrySyncEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "country-sync-pre",
      table = Tables.country_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.country_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val regionEA: ActorProducer[RegionPREP, RegionPREP.Id] =
    KafkaEnricherActor.make[RegionPREP, UUID, RegionPREP.Id, Instant](
      uniqueTag = "region-pre",
      table = Tables.regions,
      topic = topics.partiesWatchTopic,
      dataOps = RegionOps(),
      channelFactory = BasicChannelActor
        .makeChannel[RegionPREP, RegionPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart
    )

  val regionLabelEA: ActorProducer[LabelE, LabelE.Id] =
    KafkaEnricherActor.make[LabelE, UUID, LabelE.Id, Instant](
      uniqueTag = "region-label-pre",
      table = Tables.region_labels,
      topic = topics.partiesWatchTopic,
      dataOps = LabelOps(Tables.region_labels),
      channelFactory = BasicChannelActor
        .makeChannel[LabelE, LabelE.Id, Int](trace = enableTrace),
      dropBefore = systemStart
    )

  val regionSyncEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "region-sync-pre",
      table = Tables.region_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.region_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart
    )

  val cityEA: ActorProducer[CityPREP, CityPREP.Id] =
    KafkaEnricherActor.make[CityPREP, UUID, CityPREP.Id, Instant](
      uniqueTag = "city-pre",
      table = Tables.cities,
      topic = topics.partiesWatchTopic,
      dataOps = CityOps(),
      channelFactory = BasicChannelActor
        .makeChannel[CityPREP, CityPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val cityLabelEA: ActorProducer[LabelE, LabelE.Id] =
    KafkaEnricherActor.make[LabelE, UUID, LabelE.Id, Instant](
      uniqueTag = "city-label-pre",
      table = Tables.city_labels,
      topic = topics.partiesWatchTopic,
      dataOps = LabelOps(Tables.city_labels),
      channelFactory = BasicChannelActor
        .makeChannel[LabelE, LabelE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val citySyncEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "city-sync-pre",
      table = Tables.city_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.city_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val cityDistrictEA: ActorProducer[CityDistrictPREP, CityDistrictPREP.Id] =
    KafkaEnricherActor.make[CityDistrictPREP, UUID, CityDistrictPREP.Id, Instant](
      uniqueTag = "city-district-pre",
      table = Tables.city_districts,
      topic = topics.partiesWatchTopic,
      dataOps = DistrictOps(),
      channelFactory = BasicChannelActor
        .makeChannel[CityDistrictPREP, CityDistrictPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val cityDistrictLabelEA: ActorProducer[LabelE, LabelE.Id] =
    KafkaEnricherActor.make[LabelE, UUID, LabelE.Id, Instant](
      uniqueTag = "city-district-label-pre",
      table = Tables.city_district_labels,
      topic = topics.partiesWatchTopic,
      dataOps = LabelOps(Tables.city_district_labels),
      channelFactory = BasicChannelActor
        .makeChannel[LabelE, LabelE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val pickupPointEA: ActorProducer[PickupPointPREP, PickupPointPREP.Id] =
    KafkaEnricherActor.make[PickupPointPREP, UUID, PickupPointPREP.Id, Instant](
      uniqueTag = "pickup-point-pre",
      table = Tables.city_points,
      topic = topics.partiesWatchTopic,
      dataOps = PickupPointOps(),
      channelFactory = BasicChannelActor
        .makeChannel[PickupPointPREP, PickupPointPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val pickupPointSyncEA: ActorProducer[SyncE, SyncE.Id] =
    KafkaEnricherActor.make[SyncE, UUID, SyncE.Id, Instant](
      uniqueTag = "pickup-point-sync-pre",
      table = Tables.city_point_syncs,
      topic = topics.partiesWatchTopic,
      dataOps = SyncOps(Tables.city_point_syncs),
      channelFactory = BasicChannelActor
        .makeChannel[SyncE, SyncE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val pickupPointLabelEA: ActorProducer[LabelE, LabelE.Id] =
    KafkaEnricherActor.make[LabelE, UUID, LabelE.Id, Instant](
      uniqueTag = "pickup-point-label-pre",
      table = Tables.city_point_labels,
      topic = topics.partiesWatchTopic,
      dataOps = LabelOps(Tables.city_point_labels),
      channelFactory = BasicChannelActor
        .makeChannel[LabelE, LabelE.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )

  val addressEA: ActorProducer[AddressPREP, AddressPREP.Id] =
    KafkaEnricherActor.make[AddressPREP, UUID, AddressPREP.Id, Instant](
      uniqueTag = "address-pre",
      table = Tables.addresses,
      topic = topics.partiesWatchTopic,
      dataOps = AddressOps(),
      channelFactory = BasicChannelActor
        .makeChannel[AddressPREP, AddressPREP.Id, Int](trace = enableTrace),
      dropBefore = systemStart,
      enableTrace = enableTrace
    )
}

object DataOps {
  final def apply()(
      implicit bm: BaseModule,
      rm: RuntimeModule,
      tx: Managed[Throwable, HikariTransactor[Task]],
      am: AkkaModule,
      ktm: KafkaTopicModule,
      ke: KafkaEdge.KafkaEdgeWrapper
  ): DataOps = new DataOps
}
