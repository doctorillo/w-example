import React from 'react'
import useStyles from './styles'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

function SubMenu(props: any){
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  return (<div className={style.root}>
    {props.children}
  </div>)
}

export default SubMenu