import React from 'react'
import useStyles from './excursion-styles'
import BedIcon from '../../../icons/BedIcon'
import GuestIcon from '../../../icons/Guest'
import numeral from 'numeral'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'
import { PlannerExcursionItem, sortByDateTime } from '../../../../types/planner/PlannerExcursionItem'
import { formatLocalTime, formatShortLocalDate, parseLocalDate, parseLocalTime } from '../../../../types/DateRange'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

export default function ExcursionOrder(props: TripPlannerPageProps) {
  const theme = useTheme<AppTheme>()
  const styles = useStyles(theme)
  if (!props.env || !props.env.basic || !props.env.excursion){
    return null
  }
  const { env: { excursion: { excursionItems, excursionSelected } } } = props
  const items = excursionItems.reduce((a, x) => [...a, ...x.variants], [] as PlannerExcursionItem[])
  const bookingItem = items.filter(x => excursionSelected.includes(x.id)).sort(sortByDateTime)
  return (
    <div className={styles.root}>
      <div className={'title'}>Экскурсии</div>
      {bookingItem.map((x: PlannerExcursionItem, idx: number) => {
          const {
            date,
            excursionName,
            startTime,
            total,
            clients,
            accommodationPax
          } = x
          return (
            <div key={idx} className={'excursion-container'}>
              <div className={'idx-container'}>
                {idx + 1}
              </div>
              <div className={'body-container'}>
                <div className={'name'}>
                  {excursionName}
                  <div className={'date'}>
                    {formatShortLocalDate(parseLocalDate(date))}
                  </div>
                  <div className={'date'}>
                    {formatLocalTime(parseLocalTime(startTime))}
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
                  {accommodationPax > 0  && <div className={'property'}>
                    <div className={styles.icon}>
                      <BedIcon viewBox="0 0 100 100" />
                    </div>
                    <div className={styles.content}>
                      {`Размещение в ${accommodationPax}-местном номере`}
                    </div>
                  </div>}
                </div>
                <div className={'price'}>
                  Итого: {numeral(total.value).format(',')} €
                </div>
              </div>
            </div>
          )
        })}
    </div>
  )
}