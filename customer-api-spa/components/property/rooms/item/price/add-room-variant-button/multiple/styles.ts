import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    width: '100%',
    '& .button': {
      display: 'flex',
      flexFlow: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      width: '100%',
      height: '2.5rem',
      backgroundColor: theme.cssEnv.palette.secondaryLight,
      color: '#ffffff',
      fontWeight: theme.cssEnv.typo.weightRegular,
      fontSize: '1rem',
      border: 'none',
      cursor: 'pointer',
    },
    '& .disabled': {
      display: 'flex',
      flexFlow: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      width: '100%',
      height: '2.5rem',
      borderStyle: 'solid',
      borderWidth: '1px',
      borderColor: theme.cssEnv.palette.secondaryLight,
      color: theme.cssEnv.palette.secondaryLight,
      fontWeight: theme.cssEnv.typo.weightRegular,
      fontSize: '1rem',
    },
  },
}))

export default useStyles