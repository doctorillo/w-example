import React, { useEffect, useState } from 'react'
import MapGL, { NavigationControl } from 'react-map-gl'
import Marker from './marker'
import Popup from './marker-popup'
import { PropertyMapProps } from '../../../../types/property/PropertyMapProps'
import { PropertyPoint } from '../../../../types/property/PropertyPoint'
import { MapViewPortProps } from '../../../../types/property/MapViewPortProps'
import { Nullable } from '../../../../types/Nullable'

function usePropertyMap (props: PropertyMapProps) {
  const { actual, otherPoints, toProperty} = props
  const [viewport, setViewport] = useState<MapViewPortProps>(props.viewport)
  const [popup, setPopup] = useState<Nullable<PropertyPoint>>(null)
  useEffect(() => {
    setViewport({...viewport, width: props.viewport.width})
  }, [props.viewport.width])
  return (<MapGL
    {...viewport}
    scrollZoom={false}
    mapStyle="mapbox://styles/doctorillo/cjvjne9xe1etl1dqia9wi3nfh"
    mapboxApiAccessToken={'pk.eyJ1IjoiZG9jdG9yaWxsbyIsImEiOiJjanVxNGRvY3UxdzFzNDRsOHNzeTZ5czNhIn0.6ldj6OqL5a4ygEgH1qrqiw'}
    onViewportChange={(viewport) => setViewport(viewport)}
  >
    <div style={{width: 30, marginTop: 2, marginLeft: 2}}>
      <NavigationControl
        showCompass={false}
        onViewportChange={(viewport) => setViewport(viewport)} />
    </div>
    <Marker
      key={actual.id}
      zoom={16}
      {...actual}
      select={setPopup}
    />
    {otherPoints.map((x: PropertyPoint) => <Marker key={x.id} zoom={16} {...x} select={setPopup}/>)}
    {popup && <Popup zoom={16} {...popup} onClose={() => setPopup(null)} toProperty={toProperty} />}
  </MapGL>)
}

export default usePropertyMap