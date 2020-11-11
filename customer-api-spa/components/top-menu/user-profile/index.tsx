import React, { Fragment, MouseEvent, useState } from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import Menu from '@material-ui/core/Menu'
import MenuList from '@material-ui/core/MenuList'
import MenuItem from '@material-ui/core/MenuItem'
import Divider from '@material-ui/core/Divider'
import ClickAwayListener from '@material-ui/core/ClickAwayListener'
import { AppProps } from '../../../redux/modules/env/connect'
import { WorkspaceUI } from '../../../types/contexts/WorkspaceUI'
import IconButton from '@material-ui/core/IconButton'
import AccountIcon from '../../icons/AccountIcon'
import { Nullable } from '../../../types/Nullable'

const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    maxWidth: 360,
    backgroundColor: theme.palette.background.paper,
  },
}))

const userProfile = (props: AppProps) => {
  const classes = useStyles()
  const [accountEl, setAccountEl] = useState<Nullable<any>>(null)
  if (!props.appEnv) {
    return null
  }
  const { appEnv: { solverName, workspace, workspaces }, appFn: { workspaceSelect } } = props
  console.log(accountEl)
  const open = !!accountEl
  return (<Fragment>
    <IconButton
      color="primary"
      size="small"
      onClick={(e: MouseEvent) => {
        e.currentTarget && setAccountEl(e.currentTarget)
        e.stopPropagation()
      }}>
      <AccountIcon fontSize={'large'} viewBox={'0 0 100 100'}/>
    </IconButton>
    {open && <Menu
      id="simple-menu"
      anchorEl={accountEl}
      open={open}
      onClose={() => setAccountEl(null)}
      keepMounted={true}
    >
      <ClickAwayListener onClickAway={() => setAccountEl(null)}>
        <div className={classes.root}>
          <Box
            textAlign="center"
            fontSize="subtitle1.fontSize"
            fontWeight="fontWeightMedium"
            color="primary.main"
          >
            {solverName}
          </Box>
          <MenuList>
            {workspaces.map((x: WorkspaceUI) => ([
              <Divider key={`d${x.businessPartyId}`}/>,
              <MenuItem key={`mi${x.businessPartyId}`}
                        disabled={!!workspace && workspace.businessPartyId === x.businessPartyId}
                        selected={!!workspace && workspace.businessPartyId === x.businessPartyId}
                        onClick={() => workspaceSelect(x.businessPartyId)}
              >
                {x.businessParty}
              </MenuItem>,
            ]))}
          </MenuList>
        </div>
      </ClickAwayListener>
    </Menu>}
  </Fragment>)
}

export default userProfile