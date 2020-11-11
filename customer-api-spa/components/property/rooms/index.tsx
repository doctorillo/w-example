import React from 'react'
import useStyles from './styles'
import Variant from './item'
import { PropertyPageProps } from '../../../redux/modules/property/connect'
import { PriceUnitUI } from '../../../types/property/prices/PriceUnitUI'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

export default function PropertyRooms(props: PropertyPageProps) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.propertyProps){
    return null
  }
  const {
    plannerId,
    plannerPointId,
    plannerPropertyId,
    plannerClients,
    dates,
    card,
    rooms,
    prices,
    pricesInCart,
  } = props.propertyProps
  const {
    addRoomVariantToPlan,
  } = props.propertyFn
  return (
    <article id="rooms" className={style.root}>
      <div className={'header'}>
        <h3>Номера</h3>
      </div>
      {prices.map((x: PriceUnitUI) => (
        <Variant
          key={x.id}
          plannerId={plannerId}
          plannerPointId={plannerPointId}
          plannerPropertyId={plannerPropertyId}
          plannerClients={plannerClients}
          dates={dates}
          rooms={rooms}
          card={card}
          price={x}
          pricesInCart={pricesInCart}
          addRoomVariantToPlan={addRoomVariantToPlan}
        />
      ))}
    </article>
  )
}
