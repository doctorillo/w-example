import React from 'react'
import TopInfo from './top-info'
import ByPrice from './by-price'
import ByTag from './by-tag'
import ByDate from './by-dates'
import useStyles from './styles'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'
import { SearchExcursionProps } from '../../../redux/modules/excursion-search/connect'

const hotelSearchFilter: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.env){
    return null
  }
  const { tags, excursionDates } = props.env
  const hasTags = tags.length > 0
  const hasDates = excursionDates.length > 0
  return (
    <div className={style.root}>
      <TopInfo {...props} />
      <div className={style.divider} />
      {hasDates && <ByDate {...props} />}
      {hasDates && <div className={style.divider} />}
      {hasTags && <ByTag {...props} />}
      {hasTags && <div className={style.divider} />}
      {/*<ByName {...props} />
      <div className={style.divider} />*/}
      <ByPrice {...props} />
      <div className={style.divider} />
    </div>
  )
}

export default hotelSearchFilter
