import React from 'react'
import { PropertyCardUI } from '../../../types/bookings/PropertyCardUI'
import useStyles from './styles'
import Star from '../../icons/Star'
import Price from './price'
import { PriceViewMode } from '../../../types/PriceViewMode'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

export type PropertyCardProps = {
  card: PropertyCardUI;
  priceView: PriceViewMode;
  customerId: string;
  toProperty(propertyId: string, supplierId: string, customerId: string): void;
}

const propertyCard: React.FC<PropertyCardProps> = (props: PropertyCardProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const card = props.card
  const { customerId, card: {name, star, address}, priceView, toProperty } = props
  const starIt: number[] = Array(star).fill(0)
  return (<div className={style.root}>
    <div className={clsx({ photo: true })}>
      <img src="/no-img.png" alt="card"/>
    </div>
    <div className={clsx({ description: true })}>
      <div className={clsx({ main: true })}>
        <div className={clsx({ top: true })}>
          {starIt.map((_, idx: number) => <Star key={`s_${idx}`} width="19px" height="19px" fill={'#fce157'}/>)}
        </div>
        <div className={clsx({ title: true })}>
          {name} {star}*
        </div>
        <div className={clsx({ address: true })}>
          {address}
        </div>
      </div>
      <div className={clsx({ price: true })}>
        <Price
          card={card}
          customerId={customerId}
          priceView={priceView}
          toProperty={toProperty}/>
      </div>
    </div>
  </div>)
}

export default propertyCard