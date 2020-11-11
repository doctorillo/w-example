import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  button: {
    display: 'block',
    width: '100%',
    height: '2rem',
    marginTop: '1.75rem',
    backgroundColor: theme.cssEnv.palette.primary,
    color: '#fff',
    fontWeight: theme.cssEnv.typo.weightLight,
    fontSize: '1rem',
    border: 'none',
    cursor: 'pointer',
    '&:active': {
      outline: 0,
    },
    '&:focus': {
      outline: 0,
    },
  },
  disabled: {
    display: 'none',
  },
}))

export default useStyles