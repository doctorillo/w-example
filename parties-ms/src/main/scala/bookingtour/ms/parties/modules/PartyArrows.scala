package bookingtour.ms.parties.modules

import bookingtour.protocols.actors.aggregators._
import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.{LabelE, SyncE}
import bookingtour.protocols.core.values.enumeration.{AddressItem, ContextItem, PointItem, SyncItem}
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.interlook.source.geo.{CityEP, PickupPointEP, RegionEP}
import bookingtour.protocols.interlook.source.parties.{CustomerGroupEP, PartnerEP, SupplierGroupEP}
import bookingtour.protocols.parties.agg.basic._
import bookingtour.protocols.parties.agg.{basic, CustomerGroupOp, PartyOp, ProviderOp, SupplierGroupOp}
import bookingtour.protocols.parties.values._
import cats.instances.all._
import cats.syntax.option._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
final class PartyArrows private {

  implicit val countryAggFn: Aggregate3Fn[
    CountryPREP,
    SyncE,
    LabelE,
    CountryAgg
  ] = (
      a: List[CountryPREP],
      b: List[SyncE],
      c: List[LabelE]
  ) =>
    for {
      country <- a
      labels = c
        .filter(_.dataId.x === country.id.x)
        .map(_.toLabelAgg)
      syncs = b.filter(_.dataId.x === country.id.x).map(_.sync)
    } yield CountryAgg(
      id = country.id,
      name = country.name,
      labels = labels,
      syncs = syncs
    )

  implicit val regionCreateFn: Aggregate2Fn[
    SyncE,
    RegionEP,
    RegionPREP.Create
  ] = (
      a: List[SyncE],
      b: List[RegionEP]
  ) => {
    val syncs = a.flatMap(CountryPREP.fromSync(_))
    (for {
      sync   <- syncs
      region <- b.filter(_.countryId === sync.country)
    } yield RegionPREP.Create(
      sync = RegionPREP.toSync(region.id),
      name = region.name,
      countryId = sync.id
    )).distinct
  }

  implicit val cityCreateFn: Aggregate2Fn[
    SyncE,
    CityEP,
    CityPREP.Create
  ] = (
      a: List[SyncE],
      b: List[CityEP]
  ) => {
    val syncs = a.flatMap(RegionPREP.fromSync(_))
    (for {
      sync <- syncs
      city <- b.filter(_.regionId === sync.region)
    } yield CityPREP.Create(
      sync = SyncItem.InterLook(city.id.x),
      name = city.name,
      regionId = sync.id
    )).distinct
  }

  implicit val pickupPointCreateFn: Aggregate2Fn[
    SyncE,
    PickupPointEP,
    PickupPointPREP.Create
  ] = (a: List[SyncE], b: List[PickupPointEP]) => {
    val syncs = a.flatMap(CityPREP.fromSync(_))
    (for {
      sync      <- syncs
      point     <- b.filter(x => x.cityId === sync.city)
      pointType = PointItem.fromInterLook(point.typeId.x)
      if pointType =!= PointItem.Undefined
    } yield PickupPointPREP.Create(
      sync = SyncItem.InterLook(id = point.id.x),
      solverId = None,
      cityId = sync.id,
      name = point.name,
      pointType = pointType,
      location = GPoint.Prague
    )).distinct
  }

  val providerInputFn: Aggregate2Fn[
    PartnerEP,
    PartnerEP,
    PartnerEP
  ] = (
      a: List[PartnerEP],
      b: List[PartnerEP]
  ) => (a ++ b.filter(_.priceGroup.exists(_.x > 0))).distinct

  implicit val partnerAggFn: Aggregate2Fn[
    PartnerEP,
    PartnerEP,
    PartnerEP
  ] = (
      a: List[PartnerEP],
      b: List[PartnerEP]
  ) => (a ++ b).distinct

  implicit val partyCreateFn: Aggregate3Fn[
    PartnerEP,
    PartnerEP,
    PartnerEP,
    PartyPREP.Create
  ] = (
      a: List[PartnerEP],
      b: List[PartnerEP],
      c: List[PartnerEP]
  ) =>
    (for {
      d <- (a ++ b ++ c).distinct
    } yield PartyPREP.Create(
      sync = SyncItem.InterLook(d.id.x)
    )).distinct

  implicit val partyOpFn: Aggregate2Fn[
    SyncE,
    PartnerEP,
    PartyOp
  ] = (
      a: List[SyncE],
      b: List[PartnerEP]
  ) => {
    val partySyncs = a
      .flatMap(PartyPREP.fromSync(_).toList)
    (for {
      party   <- partySyncs
      partner <- b.filter(_.id === party.party)
    } yield PartyOp(
      id = party.id,
      sync = party.party,
      group = partner.priceGroup
    )).distinct
  }

  implicit val addressCreateFn: Aggregate3Fn[
    PartnerEP,
    SyncE,
    SyncE,
    AddressPREP.Create
  ] = (
      a: List[PartnerEP],
      b: List[SyncE],
      c: List[SyncE]
  ) => {
    val partySyncs = b
      .flatMap(PartyPREP.fromSync(_).toList)
    val citySyncs = c
      .flatMap(CityPREP.fromSync(_).toList)
    (for {
      partner <- a
      party   <- partySyncs.filter(_.party === partner.id)
      city    <- citySyncs.filter(_.city === partner.city)
    } yield AddressPREP.Create(
      cityId = city.id,
      partyId = party.id,
      category = AddressItem.General,
      street = partner.address,
      zip = None
    )).distinct
  }

  implicit val companyCreateFn: Aggregate2Fn[
    PartnerEP,
    SyncE,
    CompanyPREP.Create
  ] = (
      a: List[PartnerEP],
      d: List[SyncE]
  ) => {
    val partnerSyncs = d
      .flatMap(PartyPREP.fromSync(_).toList)
    (for {
      f <- a
      g <- partnerSyncs.filter(_.party === f.id)
    } yield CompanyPREP.Create(
      partyId = g.id,
      code = f.name,
      name = f.name
    )).distinct
  }

  implicit val providerCreateFn: Aggregate3Fn[
    PartnerEP,
    SyncE,
    AppContextPREP,
    ProviderPREP.Create
  ] = (
      a: List[PartnerEP],
      b: List[SyncE],
      c: List[AppContextPREP]
  ) => {
    val partnerSyncs = b
      .flatMap(PartyPREP.fromSync(_).toList)
    (for {
      provider <- a
      sync     <- partnerSyncs.filter(_.party === provider.id)
      context  <- c.filter(x => ContextItem.BaseItems.contains(x.ctxType))
    } yield ProviderPREP.Create(
      ctxId = context.id.x,
      partyId = sync.id.x
    )).distinct
  }

  implicit val providerOpFn: Aggregate3Fn[
    PartyOp,
    AppContextPREP,
    ProviderPREP,
    ProviderOp
  ] = (
      a: List[PartyOp],
      b: List[AppContextPREP],
      c: List[ProviderPREP]
  ) => {
    for {
      provider <- c
      appCtx   <- b.filter(_.id === provider.appContext)
      party    <- a.filter(_.id === provider.party)
    } yield ProviderOp(
      id = provider.id,
      party = party.id,
      partySync = party.sync,
      partyGroup = party.group,
      app = appCtx.appId,
      ctx = appCtx.id,
      context = appCtx.ctxType
    )
  }

  implicit val customerGroupCreateFn: Aggregate2Fn[
    CustomerGroupEP,
    ProviderOp,
    CustomerGroupPREP.Create
  ] = (
      a: List[CustomerGroupEP],
      b: List[ProviderOp]
  ) => {
    (for {
      group    <- a
      provider <- b.filter(x => x.partySync === group.party && x.context === group.context)
    } yield CustomerGroupPREP.Create(
      providerId = provider.id,
      code = s"${group.context.name}".some,
      notes = None,
      sync = CustomerGroupPREP.toSync(group.groupId, provider.partySync, provider.id, group.context).some
    )).distinct
  }

  implicit val customerGroupOpFn: Aggregate3Fn[
    CustomerGroupEP,
    SyncE,
    ProviderOp,
    CustomerGroupOp
  ] = (
      a: List[CustomerGroupEP],
      b: List[SyncE],
      c: List[ProviderOp]
  ) => {
    val groupSyncs = b.flatMap(CustomerGroupPREP.fromSync(_).toList)
    for {
      group <- a
      sync <- groupSyncs.filter(x =>
               x.sync === group.groupId && x.context === group.context && x.partySync === group.party
             )
      groupSyncId <- sync.sync.toList
      provider    <- c.filter(_.id === sync.provider)
    } yield CustomerGroupOp(
      id = sync.id,
      sync = groupSyncId,
      provider = provider.id,
      party = provider.party,
      partySync = provider.partySync,
      app = provider.app,
      ctx = provider.ctx,
      contextItem = provider.context
    )
  }

  implicit val customerGroupMemberCreateFn: Aggregate3Fn[
    CustomerGroupEP,
    CustomerGroupOp,
    SyncE,
    CustomerGroupMemberPREP.Create
  ] = (
      a: List[CustomerGroupEP],
      b: List[CustomerGroupOp],
      c: List[SyncE]
  ) => {
    val partnerSyncs = c.flatMap(PartyPREP.fromSync(_).toList)
    (for {
      groupInterLook <- a
      group <- b.filter(x =>
                x.partySync === groupInterLook.party && x.sync.some === groupInterLook.group && x.contextItem === groupInterLook.context
              )
      member     <- groupInterLook.members
      memberSync <- partnerSyncs.filter(_.party === member.id)
    } yield CustomerGroupMemberPREP.Create(
      groupId = group.id.x,
      partyId = memberSync.id.x
    )).distinct
  }

  implicit val supplierGroupCreateFn: Aggregate2Fn[
    SupplierGroupEP,
    ProviderOp,
    SupplierGroupPREP.Create
  ] = (
      a: List[SupplierGroupEP],
      b: List[ProviderOp]
  ) => {
    (for {
      group    <- a
      provider <- b.filter(x => x.context === group.context && x.partySync === group.party)
    } yield SupplierGroupPREP.Create(
      providerId = provider.id,
      code = s"${group.contextItem.name}".some,
      notes = None
    )).distinct
  }

  implicit val supplierGroupOpFn: Aggregate2Fn[
    SupplierGroupPREP,
    ProviderOp,
    SupplierGroupOp
  ] = (
      a: List[SupplierGroupPREP],
      b: List[ProviderOp]
  ) => {
    for {
      provider <- b
      group    <- a.filter(_.providerId === provider.id)
    } yield SupplierGroupOp(
      id = group.id,
      provider = provider.id,
      party = provider.party,
      partySync = provider.partySync,
      app = provider.app,
      ctx = provider.ctx,
      context = provider.context
    )
  }

  implicit val supplierGroupMemberCreateFn: Aggregate3Fn[
    SupplierGroupEP,
    SupplierGroupOp,
    SyncE,
    SupplierGroupMemberPREP.Create
  ] = (
      a: List[SupplierGroupEP],
      b: List[SupplierGroupOp],
      c: List[SyncE]
  ) => {
    val partySyncs = c.flatMap(PartyPREP.fromSync(_).toList)
    for {
      group          <- b
      interlookgroup <- a.filter(x => x.context === group.context && x.party === group.partySync)
      member         <- interlookgroup.members
      partySync      <- partySyncs.filter(_.party === member)
    } yield SupplierGroupMemberPREP.Create(
      groupId = group.id,
      partyId = partySync.id
    )
  }

  implicit val partyAggFn: Aggregate2Fn[
    PartyPREP,
    SyncE,
    PartyAgg
  ] = (a: List[PartyPREP], b: List[SyncE]) => {
    for {
      party <- a
      syncs = b.filter(_.dataId.x === party.id.x).map(_.sync)
    } yield PartyAgg(id = party.id, syncs = syncs)
  }

  implicit val companyAggFn: Aggregate3Fn[
    PartyAgg,
    CompanyPREP,
    AddressPREP,
    CompanyAgg
  ] = (a: List[PartyAgg], b: List[CompanyPREP], c: List[AddressPREP]) =>
    for {
      company <- b
      party   <- a.filter(_.id === company.partyId)
      address <- c.filter(_.partyId === party.id).map(AddressPREP.toAgg(_))
    } yield CompanyAgg(
      id = company.id,
      party = party,
      address = address,
      code = company.code,
      name = company.name
    )

  implicit val appContextAggFn: Aggregate2Fn[
    AppPREP,
    AppContextPREP,
    AppContextAgg
  ] = (a: List[AppPREP], b: List[AppContextPREP]) =>
    for {
      app    <- a
      appCtx <- b.filter(_.appId === app.id)
    } yield AppContextAgg(
      id = appCtx.id,
      appId = app.id,
      appIdent = app.ident,
      code = appCtx.code,
      ctxType = appCtx.ctxType
    )

  implicit val providerAggFn: Aggregate3Fn[
    AppContextAgg,
    ProviderPREP,
    CompanyAgg,
    ProviderAgg
  ] = (
      a: List[AppContextAgg],
      b: List[ProviderPREP],
      c: List[CompanyAgg]
  ) =>
    for {
      appCtx   <- a
      provider <- b.filter(_.appContext === appCtx.id)
      company  <- c.filter(_.partyId === provider.party)
    } yield ProviderAgg(
      id = provider.id,
      company = company,
      ctx = appCtx
    )

  implicit val supplierGroupFn: Aggregate2Fn[
    ProviderAgg,
    SupplierGroupPREP,
    SupplierGroupAgg
  ] = (a: List[ProviderAgg], b: List[SupplierGroupPREP]) =>
    for {
      provider <- a
      group    <- b.filter(_.providerId === provider.id)
    } yield basic.SupplierGroupAgg(
      id = group.id,
      provider = provider,
      code = group.code,
      notes = group.notes
    )

  implicit val supplierGroupDataFn: Aggregate2Fn[
    SupplierGroupAgg,
    SupplierGroupMemberPREP,
    SupplierGroupDataAgg
  ] = (a: List[SupplierGroupAgg], b: List[SupplierGroupMemberPREP]) =>
    for {
      group   <- a
      members = b.filter(_.groupId === group.id).map(_.memberId)
    } yield SupplierGroupDataAgg(
      id = group.id,
      provider = group.provider,
      code = group.code,
      notes = group.notes,
      members = members
    )

  implicit val customerGroupAggFn: Aggregate2Fn[
    ProviderAgg,
    CustomerGroupPREP,
    CustomerGroupAgg
  ] = (a: List[ProviderAgg], b: List[CustomerGroupPREP]) =>
    for {
      provider <- a
      group    <- b.filter(_.providerId === provider.id)
    } yield CustomerGroupAgg(
      id = group.id,
      provider = provider,
      code = group.code,
      notes = group.notes
    )

  implicit val customerGroupSyncedAggFn: Aggregate2Fn[
    CustomerGroupAgg,
    SyncE,
    CustomerGroupSyncedAgg
  ] = (a: List[CustomerGroupAgg], b: List[SyncE]) =>
    for {
      group <- a
      syncs = b.filter(_.dataId.x === group.id.x).map(_.sync)
    } yield CustomerGroupSyncedAgg(
      id = group.id,
      provider = group.provider,
      code = group.code,
      notes = group.notes,
      syncs = syncs
    )

  implicit val customerGroupDataAggFn: Aggregate3Fn[
    CustomerGroupSyncedAgg,
    CustomerGroupMemberPREP,
    CompanyAgg,
    CustomerGroupDataAgg
  ] = (
      a: List[CustomerGroupSyncedAgg],
      b: List[CustomerGroupMemberPREP],
      c: List[CompanyAgg]
  ) =>
    for {
      group <- a
      members = for {
        member  <- b.filter(_.groupId === group.id)
        company <- c.filter(_.partyId === member.memberId)
      } yield company
    } yield CustomerGroupDataAgg(
      id = group.id,
      provider = group.provider,
      code = group.code,
      notes = group.notes,
      syncs = group.syncs,
      members = members
    )

  implicit val providerDataAggFn: Aggregate2Fn[
    SupplierGroupDataAgg,
    CustomerGroupDataAgg,
    ProviderDataAgg
  ] = (
      a: List[SupplierGroupDataAgg],
      b: List[CustomerGroupDataAgg]
  ) =>
    for {
      provider <- (a.map(_.provider) ++ b.map(_.provider)).distinct
    } yield ProviderDataAgg(
      id = provider.id,
      company = provider.company,
      ctx = provider.ctx,
      suppliers = a.filter(_.provider.id === provider.id),
      customers = b.filter(_.provider.id === provider.id)
    )

  implicit val solverDataAggFn: Aggregate5Fn[
    ProviderDataAgg,
    SolverPREP,
    UserPREP,
    SyncE,
    PersonPREP,
    SolverDataAgg
  ] = (
      a: List[ProviderDataAgg],
      b: List[SolverPREP],
      c: List[UserPREP],
      d: List[SyncE],
      e: List[PersonPREP]
  ) =>
    for {
      solver <- b
      person <- e.filter(_.id === solver.personId)
      users  = c.filter(_.solverId === solver.id)
    } yield {
      val u = for {
        user     <- users
        provider <- a.filter(_.company.party.id === user.businessPartyId)
        syncs    = d.filter(_.dataId.x === user.id.x).map(_.sync)
      } yield UserAgg(
        id = user.id,
        provider = provider,
        syncs = syncs,
        roles = user.roles
      )
      SolverDataAgg(
        id = solver.id,
        email = solver.email,
        password = solver.password,
        person = PersonPREP.toAgg(person),
        users = u
      )
    }

  implicit val regionAggFn: Aggregate4Fn[
    CountryAgg,
    RegionPREP,
    SyncE,
    LabelE,
    RegionAgg
  ] = (
      a: List[CountryAgg],
      b: List[RegionPREP],
      c: List[SyncE],
      d: List[LabelE]
  ) =>
    for {
      region  <- b
      country <- a.find(_.id === region.countryId).toList
      labels = d
        .filter(_.dataId.x === region.id.x)
        .map(_.toLabelAgg)
      syncs = c.filter(_.dataId.x === region.id.x).map(_.sync)
      _     <- labels.headOption.zip(syncs.headOption).headOption.toList
    } yield RegionAgg(
      id = region.id,
      country = country,
      name = region.name,
      labels = labels,
      syncs = syncs
    )

  implicit val cityAggFn: Aggregate4Fn[
    RegionAgg,
    CityPREP,
    SyncE,
    LabelE,
    CityAgg
  ] = (
      a: List[RegionAgg],
      b: List[CityPREP],
      c: List[SyncE],
      d: List[LabelE]
  ) =>
    for {
      city   <- b
      region <- a.filter(_.id === city.regionId)
      labels = d
        .filter(_.dataId.x === city.id.x)
        .map(_.toLabelAgg)
      syncs = c.filter(_.dataId.x === city.id.x).map(_.sync)
    } yield CityAgg(city.id, region, city.name, labels, syncs)

  implicit val pickupPointAggFn: Aggregate3Fn[
    PickupPointPREP,
    SyncE,
    LabelE,
    PickupPointAgg
  ] = (
      a: List[PickupPointPREP],
      b: List[SyncE],
      c: List[LabelE]
  ) =>
    for {
      item <- a
      syncs = b
        .filter(_.dataId.x === item.id.x)
        .map(_.sync)
      labels = c
        .filter(_.dataId.x === item.id.x)
        .map(_.toLabelAgg)
    } yield PickupPointAgg(
      id = item.id,
      cityId = item.cityId,
      name = item.name,
      category = item.pointType,
      location = item.location,
      syncs = syncs,
      labels = labels
    )

}

object PartyArrows {
  final def apply(): PartyArrows = new PartyArrows
}
