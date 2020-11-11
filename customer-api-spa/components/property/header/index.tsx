import React from 'react'
import useStyles from './styles'
import { PropertyPageProps } from '../../../redux/modules/property/connect'
import Star from '../../icons/Star'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

const propertyHeader: React.FC<PropertyPageProps> = (props: PropertyPageProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.propertyProps){
    return null
  }
  const style = useStyles(theme)
  const { card } = props.propertyProps
  const starIt: number[] = Array(card.star).fill(0)
  const starOther: number[] = Array(5 - starIt.length).fill(0)
  return (
    <div className={style.root}>
      <div className={'header'}>
        <h3>{card.name}</h3>
        <div className={'star'}>
          {starIt.map((_, idx: number) => <Star key={`s_${idx}`} width="36px" height="36px" fill={'#fce157'}/>)}
          {starOther.map((_, idx: number) => <Star key={`s_${idx}`} width="36px" height="36px" fill={'#21a349'}/>)}
        </div>
      </div>
    </div>
  )
}

export default propertyHeader
