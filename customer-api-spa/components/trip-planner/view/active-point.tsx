import React from 'react'
import { PointOption } from '../../../types/geo/PointOption'
import { PlannerClient } from '../../../types/planner/PlannerClient'
import { LangItem } from '../../../types/LangItem'
import format from 'date-fns/format'
import parse from 'date-fns/parseISO'
import ru from 'date-fns/locale/ru'
import { DateRange } from '../../../types/DateRange'
import { Nullable } from '../../../types/Nullable'
import PlannerPropertyView from './property'
import PlannerExcursionView from './excursion'
import {
  ExcursionPlanEnv,
  PropertyPlanEnv,
  TripPlannerPageProps,
} from '../../../redux/modules/trip-planner/TripPlannerPageProps'
import { Uuid } from '../../../types/basic/Uuid'
import { Paper, Tabs, Tab } from '@material-ui/core'

export interface PointPlanViewProps {
  lang: LangItem;
  customerId: string;
  supplierId: string;
  plannerId: string;
  plannerPointId: string;
  dates: DateRange;
  plannerClients: PlannerClient[];
  plannerProperty: Nullable<PropertyPlanEnv>;
  plannerExcursion: Nullable<ExcursionPlanEnv>;
  point: PointOption;
  isActive: boolean;

  togglePreview(value: boolean): void;

  removeRoomVariant(
    plannerId: string,
    plannerPointId: string,
    plannerRoomId: string,
    id: string,
  ): void;

  toProperty(propertyId: string, supplierId: string, customerId: string): void;

  removeExcursionVariant(plannerId: Uuid,
                         plannerPointId: Uuid,
                         plannerClientId: Uuid[],
                         itemId: Uuid): void;
}

export const mapToView = (x: TripPlannerPageProps): Nullable<PointPlanViewProps> => {
  if (!x?.appProps?.appEnv) {
    return null
  }
  const { lang, customer, workspace } = x.appProps.appEnv
  if (!customer || !workspace || x.env.variantCount === 0 || !x.env.basic) {
    return null
  }
  const { plannerId, plannerPoint, plannerClients } = x.env.basic
  const p: PointPlanViewProps = {
    lang,
    customerId: customer.id,
    supplierId: workspace.businessPartyId,
    plannerId,
    plannerPointId: plannerPoint.id,
    dates: plannerPoint.dates,
    plannerClients,
    plannerProperty: x.env.property,
    plannerExcursion: x.env.excursion,
    point: customer.point,
    isActive: true,
    togglePreview: x.fn.togglePreview,
    removeRoomVariant: x.fn.removeRoomVariant,
    toProperty: x.fn.toProperty,
    removeExcursionVariant: x.fn.removeExcursionVariant,
  }
  return p
}

export default function PointPlanView(props: PointPlanViewProps) {
  const {
    dates,
    point,
    plannerProperty,
    plannerExcursion,
  } = props
  const hasTabs = ((plannerProperty?.propertyVariantCount || 0) + (plannerExcursion?.excursionVariantCount || 0)) > 1
  const [value, setValue] = React.useState(0)

  const handleChange = (event: any, newValue: any) => {
    console.log(event?.target?.value)
    setValue(newValue)
  }
  return (
    <div className={'view'}>
      <div className={'title'}>
        <div className={'location'}>{point.label}</div>
        <div className={'location'}>
          {format(parse(dates.from), 'd MMM, EEEEEE', { locale: ru })} -{' '}
          {format(parse(dates.to), 'd MMM, EEEEEE', { locale: ru })}{' '}
        </div>
      </div>
      {hasTabs && <Paper style={{ padding: '.4rem' }}>
        <Tabs
          value={value}
          onChange={handleChange}
          variant="fullWidth"
          indicatorColor="primary"
          textColor="primary"
          aria-label="icon tabs example"
        >
          <Tab label={'Отели'}/>
          <Tab label={'Экскурсии'}/>
        </Tabs>
        {value === 0 && <PlannerPropertyView {...props} />}
        {value === 1 && <PlannerExcursionView {...props} />}
      </Paper>}
      {!hasTabs && <PlannerPropertyView {...props} />}
      {!hasTabs && <PlannerExcursionView {...props} />}
    </div>
  )
}