package bookingtour.protocols.doobie.config

import bookingtour.protocols.core.db.enumeration.MssqlChangeOperation
import bookingtour.protocols.core.values.enumeration._
import bookingtour.protocols.core.values.enumeration.properties.descriptions.{
  MedicalDepartmentItem,
  TherapyProcedureItem,
  TreatmentIndicationItem
}
import bookingtour.protocols.doobie.types.JsonbToJson._
import doobie.util.{Get, Put}

/**
  * © Alexey Toroshchin 2019.
  */
trait EnumerationToDoobieOps {
  implicit final val mssqlChangeOperationG: Get[MssqlChangeOperation] =
    Get[String].map(a => MssqlChangeOperation.withValue(a.charAt(0)))

  implicit final val mssqlChangeOperationP: Put[MssqlChangeOperation] =
    Put[String].contramap(_.value.toString)

  implicit final val activityItemG: Get[ActivityItem] =
    Get[Int].map(ActivityItem.withValue)

  implicit final val activityItemP: Put[ActivityItem] =
    Put[Int].contramap(_.value)

  implicit final val addressItemG: Get[AddressItem] =
    Get[Int].map(AddressItem.withValue)

  implicit final val addressItemP: Put[AddressItem] =
    Put[Int].contramap(_.value)

  implicit final val ageItemG: Get[AgeItem] =
    Get[Int].map(AgeItem.withValue)

  implicit final val ageItemP: Put[AgeItem] =
    Put[Int].contramap(_.value)

  implicit final val amountApplyItemG: Get[AmountApplyItem] =
    Get[Int].map(AmountApplyItem.withValue)

  implicit final val amountApplyItemP: Put[AmountApplyItem] =
    Put[Int].contramap(_.value)

  implicit final val appItemG: Get[AppItem] =
    Get[Int].map(AppItem.withValue)

  implicit final val appItemP: Put[AppItem] =
    Put[Int].contramap(_.value)

  implicit final val balneoItemG: Get[BalneoItem] =
    Get[Int].map(BalneoItem.withValue)

  implicit final val balneoItemP: Put[BalneoItem] =
    Put[Int].contramap(_.value)

  implicit final val badItemG: Get[BedItem] =
    Get[Int].map(BedItem.withValue)

  implicit final val badItemP: Put[BedItem] =
    Put[Int].contramap(_.value)

  implicit final val bookingRequestItemG: Get[BookingRequestItem] =
    Get[Int].map(BookingRequestItem.withValue)

  implicit final val bookingRequestItemP: Put[BookingRequestItem] =
    Put[Int].contramap(_.value)

  implicit final val channelItemG: Get[ChannelItem] =
    Get[Int].map(ChannelItem.withValue)

  implicit final val channelItemP: Put[ChannelItem] =
    Put[Int].contramap(_.value)

  implicit final val contactItemG: Get[ContactItem] =
    Get[Int].map(ContactItem.withValue)

  implicit final val contactItemP: Put[ContactItem] =
    Put[Int].contramap(_.value)

  implicit final val contextItemG: Get[ContextItem] =
    Get[Int].map(ContextItem.withValue)

  implicit final val contextItemP: Put[ContextItem] =
    Put[Int].contramap(_.value)

  implicit final val contextRoleItemG: Get[ContextRoleItem] =
    Get[Int].map(ContextRoleItem.withValue)

  implicit final val contextRoleItemP: Put[ContextRoleItem] =
    Put[Int].contramap(_.value)

  implicit final val costTypeItemG: Get[CostTypeItem] =
    Get[Int].map(CostTypeItem.withValue)

  implicit final val costTypeItemP: Put[CostTypeItem] =
    Put[Int].contramap(_.value)

  implicit final val currencyItemG: Get[CurrencyItem] =
    Get[Int].map(CurrencyItem.withValue)

  implicit final val currencyItemP: Put[CurrencyItem] =
    Put[Int].contramap(_.value)

  implicit final val dateRangeCategoryG: Get[DateRangeCategory] =
    Get[Int].map(DateRangeCategory.withValue)

  implicit final val dateRangeCategoryP: Put[DateRangeCategory] =
    Put[Int].contramap(_.value)

  implicit final val discountItemG: Get[DiscountItem] =
    Get[Int].map(DiscountItem.withValue)

  implicit final val discountItemP: Put[DiscountItem] =
    Put[Int].contramap(_.value)

  implicit final val goodsCategoryItemG: Get[GoodsCategoryItem] =
    Get[Int].map(GoodsCategoryItem.withValue)

  implicit final val goodsCategoryItemP: Put[GoodsCategoryItem] =
    Put[Int].contramap(_.value)

  implicit final val groupCategoryItemG: Get[GroupCategoryItem] =
    Get[Int].map(GroupCategoryItem.withValue)

  implicit final val groupCategoryItemP: Put[GroupCategoryItem] =
    Put[Int].contramap(_.value)

  implicit final val groupItemG: Get[GroupItem] =
    Get[Int].map(GroupItem.withValue)

  implicit final val groupItemP: Put[GroupItem] =
    Put[Int].contramap(_.value)

  implicit final val langItemG: Get[LangItem] =
    Get[Int].map(LangItem.withValue)

  implicit final val langItemP: Put[LangItem] =
    Put[Int].contramap(_.value)

  implicit final val massageItemG: Get[MassageItem] =
    Get[Int].map(MassageItem.withValue)

  implicit final val massageItemP: Put[MassageItem] =
    Put[Int].contramap(_.value)

  implicit final val offerTypeItemG: Get[OfferTypeItem] =
    Get[Int].map(OfferTypeItem.withValue)

  implicit final val offerTypeItemP: Put[OfferTypeItem] =
    Put[Int].contramap(_.value)

  implicit final val operationChannelItemG: Get[OperationChannelItem] =
    Get[Int].map(OperationChannelItem.withValue)

  implicit final val operationChannelItemP: Put[OperationChannelItem] =
    Put[Int].contramap(_.value)

  implicit final val personGroupItemG: Get[PersonGroupItem] =
    Get[Int].map(PersonGroupItem.withValue)

  implicit final val personGroupItemP: Put[PersonGroupItem] =
    Put[Int].contramap(_.value)

  implicit final val pointItemG: Get[PointItem] =
    Get[Int].map(PointItem.withValue)

  implicit final val pointItemP: Put[PointItem] =
    Put[Int].contramap(_.value)

  implicit final val quotaItemG: Get[QuotaItem] =
    Get[Int].map(QuotaItem.withValue)

  implicit final val quotaItemP: Put[QuotaItem] =
    Put[Int].contramap(_.value)

  implicit final val roleItemG: Get[RoleItem] =
    Get[Int].map(RoleItem.withValue)

  implicit final val roleItemP: Put[RoleItem] =
    Put[Int].contramap(_.value)

  implicit final val ruleApplyItemG: Get[RuleApplyItem] =
    Get[Int].map(RuleApplyItem.withValue)

  implicit final val ruleApplyItemP: Put[RuleApplyItem] =
    Put[Int].contramap(_.value)

  implicit final val intSexItemG: Get[GenderItem] =
    Get[Int].map(GenderItem.withValue)

  implicit final val intSexItemP: Put[GenderItem] =
    Put[Int].contramap(_.value)

  implicit final val statusItemG: Get[StatusItem] =
    Get[Int].map(StatusItem.withValue)

  implicit final val statusItemP: Put[StatusItem] =
    Put[Int].contramap(_.value)

  implicit final val stopSaleItemG: Get[StopSaleItem] =
    Get[Int].map(StopSaleItem.withValue)

  implicit final val stopSaleItemP: Put[StopSaleItem] =
    Put[Int].contramap(_.value)

  implicit final val syncSourceItemG: Get[SyncSourceItem] =
    Get[Int].map(SyncSourceItem.withValue)

  implicit final val syncSourceItemP: Put[SyncSourceItem] =
    Put[Int].contramap(_.value)

  implicit final val workspaceItemG: Get[WorkspaceItem] =
    Get[Int].map(WorkspaceItem.withValue)

  implicit final val workspaceItemP: Put[WorkspaceItem] =
    Put[Int].contramap(_.value)

  implicit final val therapyProcedureItemG: Get[TherapyProcedureItem] =
    Get[Int].map(TherapyProcedureItem.withValue)

  implicit final val therapyProcedureItemP: Put[TherapyProcedureItem] =
    Put[Int].contramap(_.value)

  implicit final val treatmentIndicationItemG: Get[TreatmentIndicationItem] =
    Get[Int].map(TreatmentIndicationItem.withValue)

  implicit final val treatmentIndicationItemP: Put[TreatmentIndicationItem] =
    Put[Int].contramap(_.value)

  implicit final val medicalDepartmentItemG: Get[MedicalDepartmentItem] =
    Get[Int].map(MedicalDepartmentItem.withValue)

  implicit final val medicalDepartmentItemP: Put[MedicalDepartmentItem] =
    Put[Int].contramap(_.value)

  implicit final def syncItemG: Get[SyncItem] = classGet[SyncItem]
  implicit final def syncItemP: Put[SyncItem] = classPut[SyncItem]

  implicit final def syncItemInterLookG: Get[SyncItem.InterLook] =
    classGet[SyncItem.InterLook]
  implicit final def syncItemInterLookP: Put[SyncItem.InterLook] =
    classPut[SyncItem.InterLook]
}

object EnumerationToDoobieOps {
  def apply(): EnumerationToDoobieOps = new EnumerationToDoobieOps {}
}
