import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  panel: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    top: 0,
    marginBottom: '0.5rem',
    backgroundColor: theme.cssEnv.palette.primary,
    [theme.breakpoints.down('sm')]: {
      flexFlow: 'column',
    },
    [theme.breakpoints.up('md')]: {
      flexFlow: 'row',
      height: `calc(${theme.cssEnv.menu.heightHd} + 1rem)`,
    },
  },
}))

export default useStyles