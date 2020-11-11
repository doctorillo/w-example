import React from 'react'
import Link from 'next/link'
import useStyles from './priceStyles'
import Eating from '../../icons/Eating'
import numeral from 'numeral'
import { PropertyCardProps } from './index'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

const propertyCardPrice: React.FC<PropertyCardProps> = (props: PropertyCardProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { customerId, card: { id, supplierId, bestPrice }, toProperty } = props
  return (<Link href={'/property'}>
    <div className={style.root} onClick={() => toProperty(id, supplierId, customerId)}>
      {!bestPrice.stopSale && <div className={'title'}>
        {bestPrice.discount ? 'специальное' : 'лучшее'} предложение
      </div>}
      {bestPrice.stopSale && <div className={'title'}>
        Продажи остановлены
      </div>}
      <div className={'price'}>
        <div className={clsx({
          from: !bestPrice.stopSale,
          ['from-stop']: bestPrice.stopSale,
        })}>
          {bestPrice.resultCount > 1 && 'от'}
        </div>
        <div className={clsx({
          amount: !bestPrice.stopSale,
          ['amount-stop']: bestPrice.stopSale,
        })}>
          {/*propertyPriceView === PriceViewValue.PerRoom ? bestPrice.price.value : Math.round(bestPrice.price.value / (bestPrice.nights * bestPrice.pax))*/}
          {numeral(Math.round(bestPrice.total.value)).format('0,0')} €
        </div>
      </div>
      {bestPrice.discount && (<div className={'discount'}>
        скидка - {bestPrice.discount.value} €
      </div>)}
      <div className={'boarding'}>
        <Eating viewBox={'0 0 100 100'} width={'2em'} height={'2em'} fill={'#b9c0cd'}/>
        {bestPrice.boarding}
      </div>
      <div className={'room'}>
        {bestPrice.roomType}
      </div>
    </div>
  </Link>)
}

export default propertyCardPrice