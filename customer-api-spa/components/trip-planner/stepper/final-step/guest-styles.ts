import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'row wrap',
    '& .title': {
      paddingBottom: '.7rem',
      color: theme.cssEnv.palette.primary,
      fontSize: '2rem',
      fontWeight: theme.cssEnv.typo.weightLight,
    },
  },
  tourist: {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    width: '100%',
    paddingBottom: '.5rem',
    '& .idx': {
      display: 'flex',
      alignSelf: 'center',
      width: '1rem',
      paddingLeft: '.5rem',
      fontSize: '1.4rem',
      fontWeight: theme.cssEnv.typo.weightLight,
      color: theme.cssEnv.palette.menuTextLight,
    },
    '& .content': {
      display: 'flex',
      flexFlow: 'column wrap',
      paddingLeft: '.5rem',
      color: theme.cssEnv.palette.menuText,
      fontSize: '.9rem',
      fontWeight: theme.cssEnv.typo.weightMedium,
      '& .label': {
        paddingRight: '1rem',
        color: theme.cssEnv.palette.menuTextLight,
        fontSize: '.9rem',
        fontWeight: theme.cssEnv.typo.weightMedium,
      }
    }
  },
  icon: {
    display: 'flex',
    width: '3rem'
  },
  content: {
    display: 'flex',
    width: 'calc(100% - 3rem)',
    fontSize: '.9rem',
    color: theme.cssEnv.palette.menuTextLight,
    fontWeight: theme.cssEnv.typo.weightMedium
  }
}))

export default useStyles