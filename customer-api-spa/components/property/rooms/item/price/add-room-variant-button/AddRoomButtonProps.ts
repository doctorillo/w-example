import { DateRange } from '../../../../../../types/DateRange'
import { PlannerClient } from '../../../../../../types/planner/PlannerClient'
import { PlannerRoom } from '../../../../../../types/planner/PlannerRoom'
import { PriceUnitUI } from '../../../../../../types/property/prices/PriceUnitUI'
import { GroupPrice } from '../../../../../../redux/modules/property/connect'

export interface AddRoomButtonProps {
  plannerId: string;
  plannerPointId: string;
  plannerPropertyId: string;
  dates: DateRange;
  plannerClients: PlannerClient[];
  rooms: PlannerRoom[];
  propertyId: string;
  propertyName: string;
  propertyStar: number;
  price: PriceUnitUI;
  pricesInCart: GroupPrice[];
}