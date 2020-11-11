import React from 'react'
import TopInfo from './top-info'
import ByBoarding from './by-boardings'
import ByPrice from './by-price'
import ByName from './by-name'
import ByStar from './by-star'
import ByAmenities from './by-amenities'
import ByFacilities from './by-facilities'
import ByIndications from './by-indications'
import ByTherapies from './by-therapies'
import useStyles from './styles'
import { SearchPropertyProps } from '../../../redux/modules/property-search/connect'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

const hotelSearchFilter: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.env){
    return null
  }
  const { amenities, facilities, indications, therapies } = props.env
  const hasAmenity = amenities.length > 0
  const hasFacility = facilities.length > 0
  const hasIndication = indications.length > 0
  const hasTherapy = therapies.length > 0
  return (
    <div className={style.root}>
      <TopInfo {...props} />
      <div className={style.divider} />
      <ByStar {...props} />
      <div className={style.divider} />
      <ByName {...props} />
      <div className={style.divider} />
      <ByBoarding {...props} />
      <div className={style.divider} />
      <ByPrice {...props} />
      <div className={style.divider} />
      {hasAmenity && <ByAmenities {...props} />}
      {hasAmenity && <div className={style.divider} />}
      {hasFacility && <ByFacilities {...props} />}
      {hasFacility && <div className={style.divider} />}
      {hasIndication && <ByIndications {...props} />}
      {hasIndication && <div className={style.divider} />}
      {hasTherapy && <ByTherapies {...props} />}
      {hasTherapy && <div className={style.divider} />}
    </div>
  )
}

export default hotelSearchFilter
