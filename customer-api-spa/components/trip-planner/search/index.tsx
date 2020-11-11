import React from 'react'
import Search from './form'
import useStyle from './style'
import connect from '../../../redux/modules/trip-planner/connect'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'
import { TripPlannerPageProps } from '../../../redux/modules/trip-planner/TripPlannerPageProps'

const tripPlannerPanner: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyle(theme)
  return !props.env.createFormView ? null : <div className={style.panel}>
    <Search {...props} />
  </div>
}

export default connect(tripPlannerPanner)