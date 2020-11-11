import React from 'react'
import useStyles from './styles'
import { PriceUnitUI } from '../../../../types/property/prices/PriceUnitUI'
import Price from './price'
import Bed from '../../../icons/BedIcon'
import Eating from '../../../icons/Eating'
import { AddToPlanFn, GroupPrice } from '../../../../redux/modules/property/connect'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import { PropertyCardUI } from '../../../../types/bookings/PropertyCardUI'
import { DateRange } from '../../../../types/DateRange'
import { PlannerRoom } from '../../../../types/planner/PlannerRoom'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

export interface RoomVariantProps {
  plannerId: string;
  plannerPointId: string;
  plannerPropertyId: string;
  plannerClients: PlannerClient[];
  rooms: PlannerRoom[];
  card: PropertyCardUI;
  dates: DateRange;
  price: PriceUnitUI;
  pricesInCart: GroupPrice[];
}

export default function RoomVariant(props: RoomVariantProps & AddToPlanFn) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { price } = props
  return (
    <div className={style.root}>
      <div className={'photo'}>
        <img src="/no-img.png" alt="card"/>
      </div>
      <div className={'description'}>
        <div className={'main'}>
          <div className={'title'}>
            {price.roomTypeLabel} {price.roomCategoryLabel}
          </div>
          <div className={'subtitle'}>
            Цена по тарифу {price.tariffLabel} включает:
          </div>
          <div className={'info'}>
            <div className={'item'}>
              <div className={'icon'}>
                <Bed viewBox={'0 0 100 100'} width={'1em'} height={'1em'} color={'secondary'}/>
              </div>
              <div className={'label'}>
                {price.nights} ночей
              </div>
            </div>
            <div className={'item'}>
              <div className={'icon'}>
                <Eating viewBox={'0 0 100 100'} width={'1em'} height={'1em'} color={'secondary'}/>
              </div>
              <div className={'label'}>
                {price.boardingLabel}
              </div>
            </div>
          </div>
        </div>
        <div className={'price'}>
          <Price {...props} />
        </div>
      </div>
    </div>
  )
}
