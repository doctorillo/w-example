import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    height: '3.5rem',
    backgroundColor: theme.cssEnv.palette.subMenu,
  },
}))

export default useStyles