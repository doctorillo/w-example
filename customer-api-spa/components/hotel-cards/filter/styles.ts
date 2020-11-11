import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    width: '100%',
    backgroundColor: 'white',
    boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
  },
  divider: {
    display: 'block',
    width: '100%',
    height: '1px',
    backgroundColor: theme.cssEnv.palette.pageBackgroundDarker,
  },
}))

export default useStyles