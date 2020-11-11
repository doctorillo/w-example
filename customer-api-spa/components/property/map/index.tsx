import React, { useRef } from 'react'
import useComponentSize from '@rehooks/component-size'
import useStyles from './styles'
import { PropertyPageProps } from '../../../redux/modules/property/connect'
import Map from './item'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

const usePropertyMap: React.FC<PropertyPageProps> = (props: PropertyPageProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.propertyProps){
    return null
  }
  const { propertyProps: {point, otherPoints}, propertyFn: {toProperty} } = props
  const mainRef = useRef(null)
  const size = useComponentSize(mainRef)
  const { width } = size
  return (
    <article
      ref={mainRef}
      className={style.root}>
      <Map
        viewport={{
        width: width,
        height: 350,
        latitude: point.latitude,
        longitude: point.longitude,
        zoom: 12,
      }}
        actual={point}
        otherPoints={otherPoints}
        toProperty={toProperty}
      />
    </article>
  )
}

export default usePropertyMap
