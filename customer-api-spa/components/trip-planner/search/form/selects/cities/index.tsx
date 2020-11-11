import React, { Fragment, useEffect } from 'react'
import connect, { CitySelectProps, PointProps } from '../../../../../../redux/modules/points/connect'
import { ResultKind } from '../../../../../../types/ResultKind'
import { FormControl } from '@material-ui/core'
import InputLabel from '@material-ui/core/InputLabel'
import NativeSelect from '@material-ui/core/NativeSelect'
import { PointOption } from '../../../../../../types/geo/PointOption'

/*const useFormStyles = makeStyles<AppTheme>(theme => ({
  root: {
    height: `calc(${theme.cssEnv.menu.heightHd} - 1rem)`,
  }
}))*/

const plannerCitySelect: React.FC<PointProps & CitySelectProps> = (props: PointProps) => {
  //const theme = useTheme() as AppTheme
  //const styles = useFormStyles(theme)
  useEffect(() => {
    if (props.status === ResultKind.Undefined) {
      props.fn.fetch()
    }
  }, [props?.env?.points || []])
  if (!props.env) {
    return null
  }
  const { points } = props.env
  const { selectedPoint, selectPoint } = props.pointSelect
  return <FormControl>
    <InputLabel htmlFor={'cities-native'}>Куда</InputLabel>
    <NativeSelect
      inputProps={{
        name: 'cities',
        id: 'cities-native',
      }}
      value={selectedPoint?.value}
      onChange={event => {
        const id = event.target.value ? event.target.value : null
        if (!id){
          selectPoint(null)
        } else {
          const p = points.find((x: PointOption) => x.value === id)
          if (p){
            selectPoint(p)
          }
        }
      }}>
      <Fragment>
        <option value="" />
        {points.map((x: PointOption, idx: number) => <option key={idx} value={x.value}>
          {x.label}
        </option>)}
      </Fragment>
    </NativeSelect>
  </FormControl>
}

export default connect(plannerCitySelect)