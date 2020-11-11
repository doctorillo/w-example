import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    backgroundColor: theme.cssEnv.palette.pageBackground,
    '& .panel': {
      display: 'flex',
      flexFlow: 'row',
      justifyContent: 'center',
      paddingTop: '1rem',
      paddingBottom: '1rem',
      width: '100%',
      backgroundColor: theme.cssEnv.palette.primary,
    },
  },
  menu: {
    display: 'flex',
    flexFlow: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    width: '100%',
    '& a': {
      display: 'flex',
      color: '#ffffff',
      textDecoration: 'none',
      fontFamily: theme.cssEnv.typo.family,
      fontSize: '1rem',
    },
    '& a::hover': {
      textDecoration: 'underline',
    },
  },
  list: {
    display: 'flex',
    flexFlow: 'column wrap',
    width: '100%',
    justifyContent: 'flex-start',
    alignContent: 'flex-start',
    '& .item': {
      display: 'flex',
      width: '100%',
      paddingLeft: '1rem',
      lineHeight: '2rem',
    },
  },
}))

export default useStyles