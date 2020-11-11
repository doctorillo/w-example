import React, { Fragment } from 'react'
import useStyles from './property-styles'
import { PlannerRoom } from '../../../../types/planner/PlannerRoom'
import { PlannerAccommodationItem } from '../../../../types/planner/PlannerAccommodationItem'
import MoonIcon from '../../../icons/Moon'
import BedIcon from '../../../icons/BedIcon'
import EatingIcon from '../../../icons/Eating'
import numeral from 'numeral'
import differenceInDays from 'date-fns/differenceInDays'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { formatShortLocalDate, parseLocalDate } from '../../../../types/DateRange'
import GuestIcon from '../../../icons/Guest'

export default function PropertyOrder(props: TripPlannerPageProps) {
  const theme = useTheme<AppTheme>()
  const styles = useStyles(theme)
  if (!props.env || !props.env.basic || !props.env.property){
    return null
  }
  const { env: { basic: { plannerPoint: { dates }}, property: { propertyRooms } } } = props
  const dateFrom = parseLocalDate(dates.from)
  const dateTo = parseLocalDate(dates.to)
  const nights = differenceInDays(dateTo, dateFrom)

  return (
    <div className={styles.root}>
      <div className={'title'}>Размещение</div>
      {propertyRooms
        .filter((x: PlannerRoom) => x.selected)
        .map((x: PlannerRoom, idx: number) => {
          const y = x.variants.find(
            (y: PlannerAccommodationItem) => x.selected === y.id
          )
          if (!y) {
            return <Fragment />
          }
          const { clients } = x
          const {
            propertyName,
            propertyStar,
            roomType,
            roomCategory,
            boarding,
            price,
          } = y
          return (
            <div key={idx} className={'property-container'}>
              <div className={'idx-container'}>
                {idx + 1}
              </div>
              <div className={'body-container'}>
                <div className={'name'}>
                  {propertyName} {propertyStar}*
                  <div className={'date'}>
                    {formatShortLocalDate(dateFrom)}, 14:00 - {formatShortLocalDate(dateTo)}, 10:00. {nights} н.
                  </div>
                </div>
                <div className={'description'}>
                  <div className={'guest'}>
                    <div className={styles.icon}>
                      <GuestIcon viewBox="0 0 100 100" />
                    </div>
                    <div className={styles.content}>
                      {clients.length} чел.
                    </div>
                  </div>
                  <div className={'moon'}>
                    <div className={styles.icon}>
                      <MoonIcon viewBox="0 0 100 100" />
                    </div>
                    <div className={styles.content}>
                      {nights} ночей
                    </div>
                  </div>
                  <div className={'room'}>
                    <div className={styles.icon}>
                      <BedIcon viewBox="0 0 100 100" />
                    </div>
                    <div className={styles.content}>
                      {roomType} {roomCategory}
                    </div>
                  </div>
                  <div className={'eating'}>
                    <div className={styles.icon}>
                      <EatingIcon viewBox="0 0 100 100" />
                    </div>
                    <div className={styles.content}>
                      {boarding}
                    </div>
                  </div>
                </div>
                <div className={'price'}>
                  Итого: {numeral(price.value).format(',')} €
                </div>
              </div>
            </div>
          )
        })}
    </div>
  )
}
