import React, { ReactNode } from 'react'
import TripPlanMenuIcon from '../trip-planner/trip-plan-icon'
import TripPlanView from '../trip-planner/view'
import UserProfile from './user-profile'
import AppBar from '@material-ui/core/AppBar'
import Box from '@material-ui/core/Box'
import Toolbar from '@material-ui/core/Toolbar'
import { makeStyles } from '@material-ui/core/styles'
import { AppProps } from '../../redux/modules/env/connect'
import { AppTheme } from '../theme'

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    '& > *': {
      height: theme.spacing(8),
      backgroundColor: 'white',
    },
  },
  logo: {
    flexGrow: 1,
  },
}))

const topMenu = (props: AppProps & {children: ReactNode}) => {
  const { children } = props
  const style = useStyles()
  return (
    <header>
      <AppBar position={'fixed'} elevation={2} className={style.root}>
        <Toolbar variant={'dense'}>
          <Box display="flex" width={'75px'} flexWrap="nowrap" flexDirection={'row'}>
            <Box width={'2/3'} color={'secondary.main'} fontSize={52} fontWeight={300}>
              V
            </Box>
            <Box width={'1/3'} color={'primary.main'} fontSize={24} fontWeight={500} paddingTop={4}>
              α
            </Box>
          </Box>
          {children}
          <div className={style.logo}/>
          <TripPlanMenuIcon {...props} />
          <TripPlanView {...props} />
          <UserProfile {...props} />
        </Toolbar>
      </AppBar>
    </header>
  )
}

export default topMenu
