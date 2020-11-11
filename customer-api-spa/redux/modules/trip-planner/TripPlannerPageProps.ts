import { PlannerClient } from '../../../types/planner/PlannerClient'
import { PlannerPoint } from '../../../types/planner/PlannerPoint'
import { QueryGroup } from '../../../types/bookings/QueryGroup'
import { PlannerRoom } from '../../../types/planner/PlannerRoom'
import { QueryClientGroup } from '../../../types/bookings/QueryClientGroup'
import { LangItem } from '../../../types/LangItem'
import { Nullable } from '../../../types/Nullable'
import { PlannerSession } from '../../../types/planner/PlannerSession'
import { ResultKind } from '../../../types/ResultKind'
import { Uuid } from '../../../types/basic/Uuid'
import { PlannerSessionCreate } from '../../../types/planner/cmd/PlannerSessionCreate'
import { PlannerSessionUpdate } from '../../../types/planner/cmd/PlannerSessionUpdate'
import { GenderItem } from '../../../types/parties/GenderItem'
import { AppProps } from '../env/connect'
import { PropertyFilterParams } from '../../../types/planner/PropertyFilterParams'
import { PriceViewMode } from '../../../types/PriceViewMode'
import { ExcursionFilterParams } from '../../../types/planner/ExcursionFilterParams'
import { PlannerExcursionItem, PlannerExcursionItemId } from '../../../types/planner/PlannerExcursionItem'
import { PlannerExcursionVariantDate } from '../../../types/planner/PlannerExcursionVariantDate'

export type TripPlannerPageProps = {
  appProps: AppProps;
  env: TripPlannerEnv;
  fn: TripPlannerFn & TripPlannerPropertyVariantFn & TripPlannerExcursionVariantFn & TripPlannerClientFn;
}

export interface TripPlannerEnv {
  lang: LangItem;
  basic: Nullable<BasicPlanEnv>;
  property: Nullable<PropertyPlanEnv>;
  excursion: Nullable<ExcursionPlanEnv>;
  others: PlannerSession[];
  variantCount: number;
  createFormView: boolean;
  preview: boolean;
  status: ResultKind;
}

export interface BasicPlanEnv {
  customerId: string;
  customerName: string;
  identCode: string;
  plannerId: string;
  plannerClients: PlannerClient[];
  plannerPoint: PlannerPoint;
  plannerPoints: PlannerPoint[];
  bookingStep: number;
}

export interface PropertyPlanEnv {
  plannerPropertyId: Uuid;
  propertyQueryGroup: QueryGroup;
  propertyRooms: PlannerRoom[];
  propertyVariantCount: number;
  propertyFilter: PropertyFilterParams;
  propertyPriceView: PriceViewMode;
  propertyMaxItems: number;
}

export interface ExcursionPlanEnv {
  plannerExcursionId: Uuid;
  excursionQueryGroup: QueryClientGroup;
  excursionItems: PlannerExcursionVariantDate[];
  excursionSelected: PlannerExcursionItemId[];
  excursionVariantCount: number;
  excursionFilter: ExcursionFilterParams;
  excursionMaxItems: number;
}

export interface TripPlannerFn {
  createForm(view: boolean): void;

  fetch(solverId: Uuid): void;

  fetchPoints(): void;

  removePlan(id: string): void;

  selectPlan(id: Nullable<Uuid>): void;

  togglePreview(value: boolean): void;

  plannerCreate(cmd: PlannerSessionCreate): void;

  plannerUpdate(cmd: PlannerSessionUpdate): void;

  plannerSetBookingStep(step: number): void;
}

export interface TripPlannerPropertyVariantFn {
  toProperty(propertyId: Uuid, supplierId: Uuid, customerId: string): void;

  selectRoomVariant(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerRoomId: Uuid,
    itemId: Uuid,
  ): void;

  removeRoomVariant(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerRoomId: Uuid,
    id: Uuid,
  ): void;
}

export interface TripPlannerExcursionVariantFn {
  addExcursionToPlan(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerClients: Uuid[],
    item: PlannerExcursionItem,
  ): void;

  selectExcursionVariant(
    plannerId: Uuid,
    plannerPointId: Uuid,
    itemId: Uuid,
  ): void;

  unselectExcursionVariant(
    plannerId: Uuid,
    plannerPointId: Uuid,
    itemId: Uuid,
  ): void;

  removeExcursionVariant(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerClientId: Uuid[],
    itemId: Uuid,
  ): void;
}

export interface TripPlannerClientFn {
  setFirstName(id: Uuid, value: Nullable<string>): void;

  setLastName(id: Uuid, value: Nullable<string>): void;

  setGender(id: Uuid, value: Nullable<GenderItem>): void;

  setBirthDay(id: Uuid, value: Nullable<string>): void;

  setPassportSerial(id: Uuid, value: Nullable<string>): void;

  setPassportNumber(id: Uuid, value: Nullable<string>): void;

  setPassportExpired(id: Uuid, value: Nullable<string>): void;

  setPassportState(id: Uuid, value: Nullable<string>): void;
}
