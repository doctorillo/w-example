package bookingtour.ms.parties.modules

import bookingtour.core.actors.modules.AkkaModule
import bookingtour.core.actors.primitives.channel.basic.BasicChannelActor
import bookingtour.core.actors.primitives.channel.signal.SignalChannelActor
import bookingtour.core.actors.primitives.transforms.five.Aggregate5ChannelsActor
import bookingtour.core.actors.primitives.transforms.four.Aggregate4ChannelsActor
import bookingtour.core.actors.primitives.transforms.three.Aggregate3ChannelsActor
import bookingtour.core.actors.primitives.transforms.two.Aggregate2ChannelsActor
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core._
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import bookingtour.protocols.core.values.db.{LabelE, SyncE}
import bookingtour.protocols.parties.agg.basic._
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.parties.values._
import cats.instances.all._

/**
  * © Alexey Toroshchin 2019.
  */
final class AggregateOps private (
    implicit bm: BaseModule,
    rm: RuntimeModule,
    am: AkkaModule,
    dto: DataOps,
    arrows: PartyArrows
) {
  import am._
  import arrows._
  import bm._
  import dto._
  import rm._

  val countryAgg: ActorProducer[CountryAgg, CountryAgg.Id] =
    Aggregate3ChannelsActor.make[
      CountryPREP,
      CountryPREP.Id,
      SyncE,
      SyncE.Id,
      LabelE,
      LabelE.Id,
      CountryAgg,
      CountryAgg.Id
    ](
      uniqueTag = "countries-agg-parties",
      makeChannel0 = SignalChannelActor
        .makeChannel[CountryPREP.Id](countryEA, enableTrace),
      makeChannel1 = SignalChannelActor
        .makeChannel[SyncE.Id](countrySyncEA, enableTrace),
      makeChannel2 = SignalChannelActor.makeChannel[LabelE.Id](countryLabelEA, enableTrace),
      makeChannelState = BasicChannelActor.makeChannel[CountryAgg, CountryAgg.Id, Int](trace = enableTrace),
      channelMayBeEmpty = false,
      enableTrace = enableTrace
    )

  val partyAGG: ActorProducer[PartyAgg, PartyAgg.Id] = Aggregate2ChannelsActor
    .make[PartyPREP, PartyPREP.Id, SyncE, SyncE.Id, PartyAgg, PartyAgg.Id](
      uniqueTag = "party-agg-parties",
      makeChannel0 = SignalChannelActor.makeChannel[PartyPREP.Id](partyEA, enableTrace),
      makeChannel1 = SignalChannelActor.makeChannel[SyncE.Id](partySyncsEA, enableTrace),
      makeChannelState = BasicChannelActor.makeChannel[PartyAgg, PartyAgg.Id, Int](trace = enableTrace),
      channelMayBeEmpty = false,
      enableTrace = enableTrace
    )

  val companyAGG: ActorProducer[CompanyAgg, CompanyAgg.Id] =
    Aggregate3ChannelsActor
      .make[
        PartyAgg,
        PartyAgg.Id,
        CompanyPREP,
        CompanyPREP.Id,
        AddressPREP,
        AddressPREP.Id,
        CompanyAgg,
        CompanyAgg.Id
      ](
        uniqueTag = "company-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[PartyAgg.Id](partyAGG, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[CompanyPREP.Id](companiesEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[AddressPREP.Id](addressEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[CompanyAgg, CompanyAgg.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val appContextAGG: ActorProducer[AppContextAgg, AppContextAgg.Id] =
    Aggregate2ChannelsActor
      .make[
        AppPREP,
        AppPREP.Id,
        AppContextPREP,
        AppContextPREP.Id,
        AppContextAgg,
        AppContextAgg.Id
      ](
        uniqueTag = "app-context-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[AppPREP.Id](appEA, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[AppContextPREP.Id](appContextEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[AppContextAgg, AppContextAgg.Id, AppId](
          trace = enableTrace
        ),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val providerAGG: ActorProducer[ProviderAgg, ProviderAgg.Id] =
    Aggregate3ChannelsActor
      .make[
        AppContextAgg,
        AppContextAgg.Id,
        ProviderPREP,
        ProviderPREP.Id,
        CompanyAgg,
        CompanyAgg.Id,
        ProviderAgg,
        ProviderAgg.Id
      ](
        uniqueTag = "provider-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[AppContextAgg.Id](appContextAGG, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[ProviderPREP.Id](providerEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[CompanyAgg.Id](companyAGG, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[ProviderAgg, ProviderAgg.Id, AppContextId](
          trace = enableTrace
        ),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val supplierGroupAGG: ActorProducer[SupplierGroupAgg, SupplierGroupAgg.Id] =
    Aggregate2ChannelsActor
      .make[
        ProviderAgg,
        ProviderAgg.Id,
        SupplierGroupPREP,
        SupplierGroupPREP.Id,
        SupplierGroupAgg,
        SupplierGroupAgg.Id
      ](
        uniqueTag = "supplier-group-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[ProviderAgg.Id](providerAGG, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[SupplierGroupPREP.Id](providerSupplierGroupEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[SupplierGroupAgg, SupplierGroupAgg.Id, ProviderId](
          trace = enableTrace
        ),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val supplierGroupDataAGG: ActorProducer[SupplierGroupDataAgg, SupplierGroupDataAgg.Id] =
    Aggregate2ChannelsActor
      .make[
        SupplierGroupAgg,
        SupplierGroupAgg.Id,
        SupplierGroupMemberPREP,
        SupplierGroupMemberPREP.Id,
        SupplierGroupDataAgg,
        SupplierGroupDataAgg.Id
      ](
        uniqueTag = "supplier-group-data-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[SupplierGroupAgg.Id](supplierGroupAGG, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[SupplierGroupMemberPREP.Id](providerSupplierGroupMemberEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[SupplierGroupDataAgg, SupplierGroupDataAgg.Id, ProviderId](
            trace = enableTrace
          ),
        channelMayBeEmpty = true,
        enableTrace = enableTrace
      )

  private val customerGroupAGG: ActorProducer[CustomerGroupAgg, CustomerGroupAgg.Id] =
    Aggregate2ChannelsActor
      .make[
        ProviderAgg,
        ProviderAgg.Id,
        CustomerGroupPREP,
        CustomerGroupPREP.Id,
        CustomerGroupAgg,
        CustomerGroupAgg.Id
      ](
        uniqueTag = "customer-group-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[ProviderAgg.Id](providerAGG, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[CustomerGroupPREP.Id](providerCustomerGroupEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[CustomerGroupAgg, CustomerGroupAgg.Id, ProviderId](
          trace = enableTrace
        ),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val customerGroupSyncedAGG: ActorProducer[
    CustomerGroupSyncedAgg,
    CustomerGroupSyncedAgg.Id
  ] =
    Aggregate2ChannelsActor
      .make[
        CustomerGroupAgg,
        CustomerGroupAgg.Id,
        SyncE,
        SyncE.Id,
        CustomerGroupSyncedAgg,
        CustomerGroupSyncedAgg.Id
      ](
        uniqueTag = "customer-group-synced-agg-parties",
        makeChannel0 = SignalChannelActor.makeChannel[CustomerGroupAgg.Id](customerGroupAGG, enableTrace),
        makeChannel1 = SignalChannelActor.makeChannel[SyncE.Id](providerCustomerGroupSyncEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[CustomerGroupSyncedAgg, CustomerGroupSyncedAgg.Id, ProviderId](
            trace = enableTrace
          ),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  private val customerGroupDataAGG: ActorProducer[CustomerGroupDataAgg, CustomerGroupDataAgg.Id] =
    Aggregate3ChannelsActor
      .make[
        CustomerGroupSyncedAgg,
        CustomerGroupSyncedAgg.Id,
        CustomerGroupMemberPREP,
        CustomerGroupMemberPREP.Id,
        CompanyAgg,
        CompanyAgg.Id,
        CustomerGroupDataAgg,
        CustomerGroupDataAgg.Id
      ](
        uniqueTag = "customer-group-data-agg-parties",
        makeChannel0 = SignalChannelActor
          .makeChannel[CustomerGroupSyncedAgg.Id](customerGroupSyncedAGG, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[CustomerGroupMemberPREP.Id](providerCustomerGroupMemberEA, enableTrace),
        makeChannel2 = SignalChannelActor.makeChannel[CompanyAgg.Id](companyAGG, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[CustomerGroupDataAgg, CustomerGroupDataAgg.Id, ProviderId](
            trace = enableTrace
          ),
        channelMayBeEmpty = true,
        enableTrace = enableTrace
      )

  val providerDataAGG: ActorProducer[ProviderDataAgg, ProviderDataAgg.Id] =
    Aggregate2ChannelsActor
      .make[
        SupplierGroupDataAgg,
        SupplierGroupDataAgg.Id,
        CustomerGroupDataAgg,
        CustomerGroupDataAgg.Id,
        ProviderDataAgg,
        ProviderDataAgg.Id
      ](
        uniqueTag = "provider-data-agg-parties",
        makeChannel0 = SignalChannelActor
          .makeChannel[SupplierGroupDataAgg.Id](supplierGroupDataAGG, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[CustomerGroupDataAgg.Id](customerGroupDataAGG, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[ProviderDataAgg, ProviderDataAgg.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  val solverDataAGG: ActorProducer[SolverDataAgg, SolverDataAgg.Id] =
    Aggregate5ChannelsActor
      .make[
        ProviderDataAgg,
        ProviderDataAgg.Id,
        SolverPREP,
        SolverPREP.Id,
        UserPREP,
        UserPREP.Id,
        SyncE,
        SyncE.Id,
        PersonPREP,
        PersonPREP.Id,
        SolverDataAgg,
        SolverDataAgg.Id
      ](
        uniqueTag = "solver-data-agg-parties",
        makeChannel0 = SignalChannelActor
          .makeChannel[ProviderDataAgg.Id](providerDataAGG, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[SolverPREP.Id](solverEA, enableTrace),
        makeChannel2 = SignalChannelActor
          .makeChannel[UserPREP.Id](userEA, enableTrace),
        makeChannel3 = SignalChannelActor
          .makeChannel[SyncE.Id](solverSyncEA, enableTrace),
        makeChannel4 = SignalChannelActor.makeChannel[PersonPREP.Id](personsEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[SolverDataAgg, SolverDataAgg.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  val regionAgg: ActorProducer[RegionAgg, RegionAgg.Id] =
    Aggregate4ChannelsActor
      .make[
        CountryAgg,
        CountryAgg.Id,
        RegionPREP,
        RegionPREP.Id,
        SyncE,
        SyncE.Id,
        LabelE,
        LabelE.Id,
        RegionAgg,
        RegionAgg.Id
      ](
        uniqueTag = "regions-agg-parties",
        makeChannel0 = SignalChannelActor
          .makeChannel[CountryAgg.Id](countryAgg, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[RegionPREP.Id](regionEA, enableTrace),
        makeChannel2 = SignalChannelActor
          .makeChannel[SyncE.Id](regionSyncEA, enableTrace),
        makeChannel3 = SignalChannelActor
          .makeChannel[LabelE.Id](regionLabelEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[RegionAgg, RegionAgg.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  val cityAGG: ActorProducer[CityAgg, CityAgg.Id] =
    Aggregate4ChannelsActor
      .make[
        RegionAgg,
        RegionAgg.Id,
        CityPREP,
        CityPREP.Id,
        SyncE,
        SyncE.Id,
        LabelE,
        LabelE.Id,
        CityAgg,
        CityAgg.Id
      ](
        uniqueTag = "cities-agg-parties",
        makeChannel0 = SignalChannelActor
          .makeChannel[RegionAgg.Id](regionAgg, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[CityPREP.Id](cityEA, enableTrace),
        makeChannel2 = SignalChannelActor
          .makeChannel[SyncE.Id](citySyncEA, enableTrace),
        makeChannel3 = SignalChannelActor
          .makeChannel[LabelE.Id](cityLabelEA, enableTrace),
        makeChannelState = BasicChannelActor.makeChannel[CityAgg, CityAgg.Id, Int](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )

  val pickupPointAGG: ActorProducer[PickupPointAgg, PickupPointAgg.Id] =
    Aggregate3ChannelsActor
      .make[
        PickupPointPREP,
        PickupPointPREP.Id,
        SyncE,
        SyncE.Id,
        LabelE,
        LabelE.Id,
        PickupPointAgg,
        PickupPointAgg.Id
      ](
        uniqueTag = "pickup-point-agg-parties",
        makeChannel0 = SignalChannelActor
          .makeChannel[PickupPointPREP.Id](pickupPointEA, enableTrace),
        makeChannel1 = SignalChannelActor
          .makeChannel[SyncE.Id](pickupPointSyncEA, enableTrace),
        makeChannel2 = SignalChannelActor
          .makeChannel[LabelE.Id](pickupPointLabelEA, enableTrace),
        makeChannelState = BasicChannelActor
          .makeChannel[PickupPointAgg, PickupPointAgg.Id, CityId](trace = enableTrace),
        channelMayBeEmpty = false,
        enableTrace = enableTrace
      )
}

object AggregateOps {
  final def apply()(
      implicit bm: BaseModule,
      rm: RuntimeModule,
      am: AkkaModule,
      dto: DataOps,
      arrows: PartyArrows
  ): AggregateOps = new AggregateOps
}
