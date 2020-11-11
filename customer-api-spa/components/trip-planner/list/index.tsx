import React from 'react'
import useStyles from './styles'
import connect from '../../../redux/modules/trip-planner/connect'
import HistoryTable from './history'
import Create from '@material-ui/icons/Add'
import Close from '@material-ui/icons/Close'
import Fab from '@material-ui/core/Fab'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'
import { TripPlannerPageProps } from '../../../redux/modules/trip-planner/TripPlannerPageProps'

const tripPlannerList: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { env: { createFormView }, fn: { createForm, selectPlan }} = props
  return (
    <div className={style.root}>
      {!createFormView && <Fab
        color={'secondary'}
        size={'small'}
        onClick={() => {
          selectPlan(null)
          createForm(true)
        }}
      >
        <Create/>
      </Fab>}
      {createFormView && <Fab
        color={'primary'}
        size={'small'}
        onClick={() => {
          createForm(false)
          selectPlan(null)
        }}
      >
        <Close/>
      </Fab>}
      <HistoryTable {...props} />
    </div>
  )
}

export default connect(tripPlannerList)
