import React, { useEffect, useState } from 'react'
import Link from 'next/link'
import CitySelect from './selects/cities'
import CustomerSelect, { RelationSelectProps } from './selects/relations'
import DatesSelect from './selects/dates'
import PersonSelect from './selects/persons'
import useStyles from './styles'
import Button from './search-button'
import { Nullable } from '../../../../types/Nullable'
import { PointOption } from '../../../../types/geo/PointOption'
import { DateRangeSelection, initDateRange } from '../../../../types/DateRange'
import { QueryGroup, soloGroupInit } from '../../../../types/bookings/QueryGroup'
import { PlannerSessionCreate } from '../../../../types/planner/cmd/PlannerSessionCreate'
import formatISO from 'date-fns/formatISO'
import parseISO from 'date-fns/parseISO'
import isAfter from 'date-fns/isBefore'
import isDate from 'date-fns/isDate'
import { PartyValue } from '../../../../types/parties/PartyValue'
import { PlannerSessionUpdate } from '../../../../types/planner/cmd/PlannerSessionUpdate'
import { CitySelectProps } from '../../../../redux/modules/points/connect'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'

const tripPlannerSearch: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const env = props.env
  const plannerId = env.basic?.plannerId || null
  const { appProps } = props
  const { appEnv: { customer } } = appProps
  let qg = soloGroupInit()
  if (env.property && env.property?.propertyQueryGroup){
    qg = env.property.propertyQueryGroup
  }
  const pv: Nullable<PartyValue> = plannerId && customer ? ({ id: customer.id, name: customer.name }) : null
  const pnt = plannerId && customer ? customer.point : null
  const dr = plannerId && customer ? customer.dates : initDateRange()
  const drs: DateRangeSelection = {
    startDate: parseISO(dr.from),
    endDate: parseISO(dr.to),
    key: 'selection',
  }
  const [pointS, setPointS] = useState<Nullable<PointOption>>(pnt)
  const [relationS, setRelationS] = useState<Nullable<PartyValue>>(pv)
  const [range, setRange] = useState<DateRangeSelection>(drs)
  const [queryGroup, setQueryGroup] = useState<QueryGroup>(qg)
  useEffect(() => {
    pnt !== pointS && setPointS(pnt)
    drs !== range && setRange(drs)
    pv !== relationS && setRelationS(pv)
    qg !== queryGroup && setQueryGroup(qg)
  }, [customer, env?.property?.propertyQueryGroup])
  const cs: CitySelectProps = {
    selectedPoint: pointS,
    selectPoint(point: Nullable<PointOption>): void {
      setPointS(point)
    },
  }
  const rs: RelationSelectProps = {
    selectedParty: relationS,
    selectParty(party: Nullable<PartyValue>): void {
      setRelationS(party)
    },
  }
  const isValid: boolean = !!relationS && !!pointS && isDate(range.startDate) && isDate(range.endDate) && isAfter(range.startDate, range.endDate)
  const makeSession = () => {
    if (!isValid || !pointS || !relationS) {
      return
    }
    if (!plannerId){
      const cmd: PlannerSessionCreate = {
        solverId: appProps.appEnv.solverId,
        customerId: relationS.id,
        customerName: relationS.name,
        point: pointS,
        dates: {
          from: formatISO(range.startDate, { representation: 'date' }),
          to: formatISO(range.endDate, { representation: 'date' }),
        },
        queryGroup: queryGroup,
        withExcursion: true,
        withTransfer: true,
      }
      props.fn.plannerCreate(cmd)
    } else {
      const cmd: PlannerSessionUpdate = {
        plannerId: plannerId,
        solverId: appProps.appEnv.solverId,
        customerId: relationS.id,
        customerName: relationS.name,
        point: pointS,
        dates: {
          from: formatISO(range.startDate, { representation: 'date' }),
          to: formatISO(range.endDate, { representation: 'date' }),
        },
        queryGroup: queryGroup,
      }
      props.fn.plannerUpdate(cmd)
    }
  }
  return <div className={style.root}>
    <div className={'agent'}>
      <CustomerSelect {...props.appProps} {...rs} />
    </div>
    <div className={'city'}>
      <CitySelect {...props.appProps} {...cs} />
    </div>
    <div className={'dates'}>
      <DatesSelect range={range} update={setRange}/>
    </div>
    <div className={'person'}>
      <PersonSelect queryGroup={queryGroup} update={setQueryGroup}/>
    </div>
    <div className={'btn'}>
      <Link href={'/hotels'}>
        <Button disabled={!isValid} onClick={makeSession} label='Показать'/>
      </Link>
    </div>
  </div>
}

export default tripPlannerSearch
