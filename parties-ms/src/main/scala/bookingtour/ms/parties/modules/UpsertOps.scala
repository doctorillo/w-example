package bookingtour.ms.parties.modules

import java.util.UUID

import scala.jdk.CollectionConverters._

import bookingtour.core.actors.modules.AkkaModule
import bookingtour.core.actors.primitives.channel.basic.BasicChannelActor
import bookingtour.core.actors.primitives.channel.signal.SignalChannelActor
import bookingtour.core.actors.primitives.transforms.three.Aggregate3ChannelsActor
import bookingtour.core.actors.primitives.transforms.transform.TransformChannelActor
import bookingtour.core.actors.primitives.transforms.two.Aggregate2ChannelsActor
import bookingtour.core.actors.primitives.upserters.batch.In1BatchUpsertActor
import bookingtour.core.doobie.modules.DataModule
import bookingtour.core.doobie.queries.BatchCreateVoid
import bookingtour.data.parties.sql.contexts.app.AppPartnerCreate
import bookingtour.data.parties.sql.contexts.appcontexts.AppContextCreate
import bookingtour.data.parties.sql.geo.cities.CityCreate
import bookingtour.data.parties.sql.geo.countries.CountryCreate
import bookingtour.data.parties.sql.geo.points.PickupPointCreate
import bookingtour.data.parties.sql.geo.regions.RegionCreate
import bookingtour.data.parties.sql.parties.address.AddressCreate
import bookingtour.data.parties.sql.parties.companies.CompanyCreate
import bookingtour.data.parties.sql.parties.customergroups.CustomerGroupCreate
import bookingtour.data.parties.sql.parties.customermembers.CustomerGroupMemberCreate
import bookingtour.data.parties.sql.parties.parties.PartyCreate
import bookingtour.data.parties.sql.parties.providers.ProviderCreate
import bookingtour.data.parties.sql.parties.suppliergroups.SupplierGroupCreate
import bookingtour.data.parties.sql.parties.suppliermembers.SupplierGroupMemberCreate
import bookingtour.data.parties.sql.solvers.SolverCreate
import bookingtour.ms.parties.modules.UpsertOps.SolverConfig
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.aggregators.Aggregate2Fn
import bookingtour.protocols.core._
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem._
import bookingtour.protocols.core.values.enumeration.{AppItem, LangItem}
import bookingtour.protocols.interlook.source.geo.{CityEP, CountryEP, PickupPointEP, RegionEP}
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.interlook.source.parties.{CustomerGroupEP, PartnerEP, SupplierGroupEP}
import bookingtour.protocols.parties.Tables
import bookingtour.protocols.parties.agg.basic.CompanyAgg
import bookingtour.protocols.parties.agg.{CustomerGroupOp, PartyOp, ProviderOp, SupplierGroupOp}
import bookingtour.protocols.parties.values.SolverPREP.UserCreate
import bookingtour.protocols.parties.values._
import cats.instances.all._
import cats.syntax.order._
import com.github.t3hnar.bcrypt._
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
final class UpsertOps private (
    implicit bm: BaseModule,
    dm: DataModule,
    am: AkkaModule,
    rm: RuntimeModule,
    sco: ConsumerOps,
    dto: DataOps,
    agg: AggregateOps,
    arrows: PartyArrows
) {
  import agg._
  import am._
  import arrows._
  import bm._
  import dto._
  import rm._
  import sco._

  private val appCreate: BatchCreateVoid[AppPREP.Create] =
    BatchCreateVoid.instance(AppPartnerCreate())

  appCreate.run(List(AppPREP.Create(ident = AppItem.Partner, List(LangItem.En, LangItem.Ru)))) {
    case Left(thr) =>
      log.error("AppItem upsert. {}", thr)
    case Right(_) =>
      log.info("AppItem upsert. complete.")
  }

  // app contexts

  private val appContextCreate: ActorProducer[AppContextPREP.Create, AppContextPREP.Create] =
    TransformChannelActor.makeMany[AppPREP, AppContextPREP.Create, AppContextPREP.Create](
      uniqueTag = "app-context-parties",
      producer = appEA,
      makeChannelState = BasicChannelActor
        .makeChannel[AppContextPREP.Create, AppContextPREP.Create, Int](trace = enableTrace),
      enableTrace = enableTrace
    )

  In1BatchUpsertActor
    .make[AppContextPREP.Create, AppContextPREP.Create](
      uniqueTag = "app-ctx-upsert",
      producer0 = appContextCreate,
      createOps = AppContextCreate(),
      enableTrace = enableTrace
    )

  // countries

  private val countryCreate: ActorProducer[CountryPREP.Create, CountryPREP.Create] =
    TransformChannelActor.makeMapper[CountryEP, CountryPREP.Create, CountryPREP.Create](
      uniqueTag = "country-create-parties",
      producer = countryConsumer,
      makeChannelState = BasicChannelActor
        .makeChannel[CountryPREP.Create, CountryPREP.Create, Int](trace = enableTrace),
      enableTrace = enableTrace
    )

  In1BatchUpsertActor.make[CountryPREP.Create, CountryPREP.Create](
    uniqueTag = "country-upsert",
    producer0 = countryCreate,
    createOps = CountryCreate(Tables.country_syncs, Tables.country_labels),
    enableTrace = enableTrace
  )

  // regions

  private val regionCreate: ActorProducer[RegionPREP.Create, RegionPREP.Create] =
    Aggregate2ChannelsActor
      .make[SyncE, SyncE.Id, RegionEP, RegionEP.Id, RegionPREP.Create, RegionPREP.Create](
        uniqueTag = "region-create-parties",
        makeChannel0 = SignalChannelActor.makeChannel[SyncE.Id](countrySyncEA, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[RegionEP.Id](regionConsumer, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[RegionPREP.Create, RegionPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[RegionPREP.Create, RegionPREP.Create](
    uniqueTag = "region-upsert",
    producer0 = regionCreate,
    createOps = RegionCreate(Tables.region_syncs, Tables.region_labels),
    enableTrace = enableTrace
  )

  // cities

  private val cityCreate: ActorProducer[CityPREP.Create, CityPREP.Create] = Aggregate2ChannelsActor
    .make[SyncE, SyncE.Id, CityEP, CityEP.Id, CityPREP.Create, CityPREP.Create](
      uniqueTag = "region-create-parties",
      makeChannel0 = SignalChannelActor.makeChannel[SyncE.Id](regionSyncEA, enableTrace),
      makeChannel1 = SignalChannelActor.makeChannel[CityEP.Id](cityConsumer, enableTrace),
      makeChannelState = BasicChannelActor.makeChannel[CityPREP.Create, CityPREP.Create, Int](trace = enableTrace),
      channelMayBeEmpty = false,
      enableTrace = enableTrace
    )

  In1BatchUpsertActor.make[CityPREP.Create, CityPREP.Create](
    uniqueTag = "city-upsert",
    producer0 = cityCreate,
    createOps = CityCreate(Tables.city_syncs, Tables.city_labels),
    enableTrace = enableTrace
  )

  // pickup point

  private val pickupPointCreate: ActorProducer[PickupPointPREP.Create, PickupPointPREP.Create] =
    Aggregate2ChannelsActor
      .make[
        SyncE,
        SyncE.Id,
        PickupPointEP,
        PickupPointEP.Id,
        PickupPointPREP.Create,
        PickupPointPREP.Create
      ](
        uniqueTag = "pickup-point-create-parties",
        makeChannel0 = SignalChannelActor.makeChannel[SyncE.Id](citySyncEA, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[PickupPointEP.Id](pickupPointConsumer, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[PickupPointPREP.Create, PickupPointPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[PickupPointPREP.Create, PickupPointPREP.Create](
    uniqueTag = "pickup-point-upsert",
    producer0 = pickupPointCreate,
    createOps = PickupPointCreate(Tables.city_point_syncs, Tables.city_point_labels),
    enableTrace = enableTrace
  )

  // parties
  private val providerInput: ActorProducer[PartnerEP, PartnerEP.Id] =
    Aggregate2ChannelsActor
      .makeWithMerge[
        PartnerEP,
        PartnerEP.Id,
        PartnerEP,
        PartnerEP.Id,
        PartnerEP,
        PartnerEP.Id
      ](
        uniqueTag = "partner-provider-parties",
        makeChannel0 = SignalChannelActor.makeChannel[PartnerEP.Id](branchConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[PartnerEP.Id](partnerConsumer, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[PartnerEP, PartnerEP.Id, Int](trace = enableTrace),
        mergeFn = providerInputFn,
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val partnerAgg: ActorProducer[PartnerEP, PartnerEP.Id] =
    Aggregate2ChannelsActor
      .make[
        PartnerEP,
        PartnerEP.Id,
        PartnerEP,
        PartnerEP.Id,
        PartnerEP,
        PartnerEP.Id
      ](
        uniqueTag = "partner-all-parties",
        makeChannel0 = SignalChannelActor.makeChannel[PartnerEP.Id](providerInput, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[PartnerEP.Id](propertyConsumer, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[PartnerEP, PartnerEP.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val partyCreate: ActorProducer[PartyPREP.Create, PartyPREP.Create] =
    TransformChannelActor
      .makeMapper[
        PartnerEP,
        PartyPREP.Create,
        PartyPREP.Create
      ](
        uniqueTag = "party-create-parties",
        producer = partnerConsumer,
        makeChannelState = BasicChannelActor
          .makeChannel[PartyPREP.Create, PartyPREP.Create, Int](trace = enableTrace),
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[PartyPREP.Create, PartyPREP.Create](
    uniqueTag = "party-upsert",
    producer0 = partyCreate,
    createOps = PartyCreate(Tables.party_syncs),
    enableTrace = enableTrace
  )

  private val partyOp: ActorProducer[
    PartyOp,
    PartyOp.Id
  ] =
    Aggregate2ChannelsActor
      .make[
        SyncE,
        SyncE.Id,
        PartnerEP,
        PartnerEP.Id,
        PartyOp,
        PartyOp.Id
      ](
        uniqueTag = "party-op",
        makeChannel0 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[PartnerEP.Id](partnerConsumer, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[PartyOp, PartyOp.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  // address

  private val addressCreate: ActorProducer[AddressPREP.Create, AddressPREP.Create] =
    Aggregate3ChannelsActor
      .make[
        PartnerEP,
        PartnerEP.Id,
        SyncE,
        SyncE.Id,
        SyncE,
        SyncE.Id,
        AddressPREP.Create,
        AddressPREP.Create
      ](
        uniqueTag = "address-create-parties",
        makeChannel0 = SignalChannelActor.makeChannel[PartnerEP.Id](partnerConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[SyncE.Id](citySyncEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[AddressPREP.Create, AddressPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[AddressPREP.Create, AddressPREP.Create](
    uniqueTag = "address-upsert",
    producer0 = addressCreate,
    createOps = AddressCreate(),
    enableTrace = enableTrace
  )

  // companies

  private val companyCreate: ActorProducer[CompanyPREP.Create, CompanyPREP.Create] =
    Aggregate2ChannelsActor
      .make[
        PartnerEP,
        PartnerEP.Id,
        SyncE,
        SyncE.Id,
        CompanyPREP.Create,
        CompanyPREP.Create
      ](
        uniqueTag = "company-create-parties",
        makeChannel0 = SignalChannelActor.makeChannel[PartnerEP.Id](partnerAgg, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[CompanyPREP.Create, CompanyPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[CompanyPREP.Create, CompanyPREP.Create](
    uniqueTag = "company-upsert",
    producer0 = companyCreate,
    createOps = CompanyCreate(),
    enableTrace = enableTrace
  )

  // providers
  private val providerCreate: ActorProducer[ProviderPREP.Create, ProviderPREP.Create] =
    Aggregate3ChannelsActor
      .make[
        PartnerEP,
        PartnerEP.Id,
        SyncE,
        SyncE.Id,
        AppContextPREP,
        AppContextPREP.Id,
        ProviderPREP.Create,
        ProviderPREP.Create
      ](
        uniqueTag = "provider-create-parties",
        makeChannel0 = SignalChannelActor.makeChannel[PartnerEP.Id](providerInput, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[AppContextPREP.Id](appContextEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[ProviderPREP.Create, ProviderPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[ProviderPREP.Create, ProviderPREP.Create](
    uniqueTag = "provider-upsert",
    producer0 = providerCreate,
    createOps = ProviderCreate(),
    enableTrace = enableTrace
  )

  private val providerOp: ActorProducer[
    ProviderOp,
    ProviderOp.Id
  ] =
    Aggregate3ChannelsActor
      .make[
        PartyOp,
        PartyOp.Id,
        AppContextPREP,
        AppContextPREP.Id,
        ProviderPREP,
        ProviderPREP.Id,
        ProviderOp,
        ProviderOp.Id
      ](
        uniqueTag = "provider-op",
        makeChannel0 = SignalChannelActor.makeChannel[PartyOp.Id](partyOp, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[AppContextPREP.Id](appContextEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[ProviderPREP.Id](providerEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[ProviderOp, ProviderOp.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  // customer groups

  private val customerGroupCreate: ActorProducer[
    CustomerGroupPREP.Create,
    CustomerGroupPREP.Create
  ] =
    Aggregate2ChannelsActor
      .make[
        CustomerGroupEP,
        CustomerGroupEP.Id,
        ProviderOp,
        ProviderOp.Id,
        CustomerGroupPREP.Create,
        CustomerGroupPREP.Create
      ](
        uniqueTag = "customer-group-parties",
        makeChannel0 = SignalChannelActor.makeChannel[CustomerGroupEP.Id](customerGroupConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[ProviderOp.Id](providerOp, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[CustomerGroupPREP.Create, CustomerGroupPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[CustomerGroupPREP.Create, CustomerGroupPREP.Create](
    uniqueTag = "customer-group-upsert",
    producer0 = customerGroupCreate,
    createOps = CustomerGroupCreate(Tables.provider_customer_group_syncs),
    enableTrace = enableTrace
  )

  val customerGroupOp: ActorProducer[
    CustomerGroupOp,
    CustomerGroupOp.Id
  ] =
    Aggregate3ChannelsActor
      .make[
        CustomerGroupEP,
        CustomerGroupEP.Id,
        SyncE,
        SyncE.Id,
        ProviderOp,
        ProviderOp.Id,
        CustomerGroupOp,
        CustomerGroupOp.Id
      ](
        uniqueTag = "customer-group-op",
        makeChannel0 = SignalChannelActor.makeChannel[CustomerGroupEP.Id](customerGroupConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[SyncE.Id](providerCustomerGroupSyncEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[ProviderOp.Id](providerOp, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[CustomerGroupOp, CustomerGroupOp.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  // customer group members

  private val customerGroupMemberCreate: ActorProducer[
    CustomerGroupMemberPREP.Create,
    CustomerGroupMemberPREP.Create
  ] =
    Aggregate3ChannelsActor
      .make[
        CustomerGroupEP,
        CustomerGroupEP.Id,
        CustomerGroupOp,
        CustomerGroupOp.Id,
        SyncE,
        SyncE.Id,
        CustomerGroupMemberPREP.Create,
        CustomerGroupMemberPREP.Create
      ](
        uniqueTag = "customer-group-member-parties",
        makeChannel0 = SignalChannelActor.makeChannel[CustomerGroupEP.Id](customerGroupConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[CustomerGroupOp.Id](customerGroupOp, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[CustomerGroupMemberPREP.Create, CustomerGroupMemberPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[CustomerGroupMemberPREP.Create, CustomerGroupMemberPREP.Create](
    uniqueTag = "customer-group-member-upsert",
    producer0 = customerGroupMemberCreate,
    createOps = CustomerGroupMemberCreate(),
    enableTrace = enableTrace
  )

  // supplier groups
  private val supplierGroupCreate: ActorProducer[
    SupplierGroupPREP.Create,
    SupplierGroupPREP.Create
  ] =
    Aggregate2ChannelsActor
      .make[
        SupplierGroupEP,
        SupplierGroupEP.Id,
        ProviderOp,
        ProviderOp.Id,
        SupplierGroupPREP.Create,
        SupplierGroupPREP.Create
      ](
        uniqueTag = "supplier-group-parties",
        makeChannel0 = SignalChannelActor.makeChannel[SupplierGroupEP.Id](supplierGroupConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[ProviderOp.Id](providerOp, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[SupplierGroupPREP.Create, SupplierGroupPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[SupplierGroupPREP.Create, SupplierGroupPREP.Create](
    uniqueTag = "supplier-group-upsert",
    producer0 = supplierGroupCreate,
    createOps = SupplierGroupCreate(),
    enableTrace = enableTrace
  )

  private val supplierGroupOp: ActorProducer[
    SupplierGroupOp,
    SupplierGroupOp.Id
  ] =
    Aggregate2ChannelsActor
      .make[
        SupplierGroupPREP,
        SupplierGroupPREP.Id,
        ProviderOp,
        ProviderOp.Id,
        SupplierGroupOp,
        SupplierGroupOp.Id
      ](
        uniqueTag = "supplier-group-op",
        makeChannel0 = SignalChannelActor
          .makeChannel[SupplierGroupPREP.Id](providerSupplierGroupEA, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[ProviderOp.Id](providerOp, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[SupplierGroupOp, SupplierGroupOp.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  // supplier group members

  private val supplierGroupMemberCreate: ActorProducer[
    SupplierGroupMemberPREP.Create,
    SupplierGroupMemberPREP.Create
  ] =
    Aggregate3ChannelsActor
      .make[
        SupplierGroupEP,
        SupplierGroupEP.Id,
        SupplierGroupOp,
        SupplierGroupOp.Id,
        SyncE,
        SyncE.Id,
        SupplierGroupMemberPREP.Create,
        SupplierGroupMemberPREP.Create
      ](
        uniqueTag = "supplier-group-member-parties",
        makeChannel0 = SignalChannelActor.makeChannel[SupplierGroupEP.Id](supplierGroupConsumer, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[SupplierGroupOp.Id](supplierGroupOp, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP.Create](
    uniqueTag = "supplier-group-member-upsert",
    producer0 = supplierGroupMemberCreate,
    createOps = SupplierGroupMemberCreate(),
    enableTrace = enableTrace
  )

  // solvers

  private val syncIdOpt: Config => Option[LookPartyId] = x => {
    if (x.hasPath("lookId")) {
      Some(x.getInt("lookId"))
    } else {
      None
    }
  }
  private val nameOpt: Config => Option[String] = x => {
    if (x.hasPath("name")) {
      Some(x.getString("name"))
    } else {
      None
    }
  }

  private val solvers = for {
    o         <- appConfig.getConfigList("applyUser").asScala.toList
    lookId    = syncIdOpt(o)
    email     = o.getString("email")
    password  = o.getString("password").bcrypt
    firstName = o.getString("firstName")
    lastName  = o.getString("lastName")
    companies = o
      .getConfigList("companies")
      .asScala
      .toList
      .map(co =>
        (
          syncIdOpt(co),
          nameOpt(co),
          co.getIntList("roles").asScala.toList.map((x: Integer) => x.intValue())
        )
      )
  } yield SolverConfig(
    sync = lookId,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    companies = companies
  )

  private val solverCreateArrFn: List[SolverConfig] => Aggregate2Fn[
    AppPREP,
    CompanyAgg,
    SolverPREP.Create
  ] = sxs =>
    (a: List[AppPREP], b: List[CompanyAgg]) => {
      val companies = b.map { x =>
        val sync =
          x.party.syncs.flatMap(_.toInterLook.map(z => LookPartyId(z.id)).toList).headOption
        (x.party.id, x.name, sync)
      }
      for {
        app    <- a.filter(_.ident === AppItem.Partner)
        solver <- sxs
        users = for {
          (partySync, name, roles) <- solver.companies
          company <- companies.filter(x =>
                      (partySync.isDefined && partySync === x._3) || (name.isDefined && name.contains(x._2))
                    )
        } yield UserCreate(
          companyPartyId = company._1,
          roles = roles
        )
      } yield SolverPREP.Create(
        appId = app.id,
        syncId = solver.sync.map(_.x),
        personId = UUID.randomUUID(),
        email = solver.email,
        passwordHash = solver.password,
        preferredLang = LangItem.Ru.value,
        firstName = solver.firstName,
        lastName = solver.lastName,
        users = users
      )
    }

  implicit private val solverCreateFn: Aggregate2Fn[AppPREP, CompanyAgg, SolverPREP.Create] =
    solverCreateArrFn(solvers)

  private val solverCreate: ActorProducer[SolverPREP.Create, SolverPREP.Create] =
    Aggregate2ChannelsActor
      .make[
        AppPREP,
        AppPREP.Id,
        CompanyAgg,
        CompanyAgg.Id,
        SolverPREP.Create,
        SolverPREP.Create
      ](
        uniqueTag = "solver-create-parties",
        makeChannel0 = SignalChannelActor.makeChannel[AppPREP.Id](appEA, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[CompanyAgg.Id](companyAGG, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[SolverPREP.Create, SolverPREP.Create, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  In1BatchUpsertActor.make[SolverPREP.Create, SolverPREP.Create](
    uniqueTag = "solver-upsert",
    producer0 = solverCreate,
    createOps = SolverCreate(Tables.solver_syncs),
    enableTrace = enableTrace
  )
}

object UpsertOps {
  final case class SolverConfig(
      sync: Option[LookPartyId],
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      companies: List[(Option[LookPartyId], Option[String], List[Int])]
  )
  final def apply()(
      implicit bm: BaseModule,
      dm: DataModule,
      am: AkkaModule,
      rm: RuntimeModule,
      sco: ConsumerOps,
      dto: DataOps,
      agg: AggregateOps,
      arrows: PartyArrows
  ): UpsertOps = new UpsertOps
}
