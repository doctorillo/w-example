import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    height: '2rem',
    backgroundColor: theme.cssEnv.palette.accent,
    color: theme.cssEnv.palette.menuText,
    fontWeight: theme.cssEnv.typo.weightMedium,
    fontSize: '1rem',
    border: 'none',
    cursor: 'pointer',
    '&:active': {
      outline: '0',
    },
    '&:focus': {
      outline: '0',
    },
  },
  disabled: {
    display: 'none',
  }
}))


export default useStyles