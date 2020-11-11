import React from 'react'
import useStyles from './styles'
import format from 'date-fns/format'
import ru from 'date-fns/locale/ru'
import Guest from './guest'
import Property from './property'
import Excursion from './excursion'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'
import numeral from 'numeral'

export default function FinalStep (props: TripPlannerPageProps) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.env || !props.env.basic){
    return null
  }
  const { env: { basic: { identCode }, property, excursion } } = props
  const propertyTotal = !property ? 0 : property.propertyRooms.reduce((a, x) => {
    if (!x.selected){
      return a
    }
    const z = x.variants.find(y => y.id === x.selected)
    if (!z){
      return a
    }
    return a + z.price.value
  }, 0)
  const excursionTotal = !excursion ? 0 : excursion.excursionItems.reduce((a, x) => {
    const selected = x.variants.filter(z => excursion.excursionSelected.includes(z.id))
    return a + selected.reduce((a, x) => a + x.total.value, 0)
  }, 0)
  const total = propertyTotal + excursionTotal
  return (<div className={style.root}>
    <div className={'header'}>
      <div className={'title'}>
        Заявка № {identCode}
      </div>
      <div className={'date'}>
        {format(new Date(), 'd MMM yyyy', {locale: ru})}
      </div>
    </div>
    <Guest {...props} />
    <Property {...props} />
    <Excursion {...props} />
    <div className={'total'}>
      <div className={'label'}>
        итого к оплате:
      </div>
      {numeral(total).format(',')} €
    </div>
  </div>)
}