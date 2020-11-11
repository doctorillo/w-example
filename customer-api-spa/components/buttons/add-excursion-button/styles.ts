import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    backgroundColor: theme.cssEnv.palette.secondaryLight
  }
}))

export default useStyles
