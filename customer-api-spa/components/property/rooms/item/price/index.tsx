import React from 'react'
import useStyles from './styles'
import numeral from 'numeral'
import { RoomVariantProps } from '../index'
import AddToCart from './add-room-variant-button/single'
import { AddToPlanFn } from '../../../../../redux/modules/property/connect'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../theme'
import clsx from 'clsx'

const variantPrice: React.FC<RoomVariantProps & AddToPlanFn> = (props: RoomVariantProps & AddToPlanFn) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const {
    plannerId,
    plannerPointId,
    plannerPropertyId,
    plannerClients,
    dates,
    rooms,
    card,
    pricesInCart,
    addRoomVariantToPlan,
    price,
    price: { stopSale, discount },
  } = props
  return (
    <div className={style.root}>
      <div className={'price'}>
        <div className={'discount'}>
          {discount &&
            numeral(Math.round(price.total.value + discount.value)).format(
              '0,0'
            )}
        </div>
        <div
          className={clsx({
            amount: !stopSale,
            ['amount-stop']: stopSale,
          })}
        >
          {numeral(Math.round(price.total.value)).format('0,0')}{' '}
          <span className={'currency'}>€</span>
        </div>
      </div>
      {stopSale && <div className={'stop'}>Продажы остановлены</div>}
      <AddToCart
        plannerId={plannerId}
        plannerPointId={plannerPointId}
        plannerPropertyId={plannerPropertyId}
        propertyId={card.id}
        propertyName={card.name}
        propertyStar={card.star}
        dates={dates}
        plannerClients={plannerClients}
        rooms={rooms}
        price={price}
        pricesInCart={pricesInCart}
        addRoomVariantToPlan={addRoomVariantToPlan}
      />
    </div>
  )
}

export default variantPrice
