import React from 'react'
import { Popup } from 'react-map-gl'
import { PropertyPoint } from '../../../../../types/property/PropertyPoint'
import useStyles from './styles'

function PropertyMarkerPopup (props: PropertyPoint & { zoom: number, toProperty: (a:string, b: string, c: string) => void, onClose: () => void }) {
  const { id, supplierId, customerId, name, star, price, latitude, longitude, toProperty, onClose } = props
  const style = useStyles()
  return (<Popup
    tipSize={15}
    anchor="top"
    longitude={longitude}
    latitude={latitude}
    captureClick={false}
    captureDrag={false}
    captureDoubleClick={false}
    closeOnClick={false}
    onClose={onClose}
  >
    <div className={style.root}>
      <div className={'title'}>
        {name} {star}*
      </div>
      <div className={'label'}>
        цена от {price} €
      </div>
      <div className={'link'}>
        <a href={'#'} onClick={() => {
          onClose()
          toProperty(id, supplierId, customerId)
        }}>
          Перейти
        </a>
      </div>
    </div>
  </Popup>)
}

export default PropertyMarkerPopup