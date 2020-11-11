import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    height: '100%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    margin: '0',
    padding: '0',
    '& li': {
      display: 'flex',
      listStyleType: 'none',
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginLeft: '1rem',
      '& a': {
        display: 'block',
        textDecoration: 'none',
        color: theme.cssEnv.palette.text,
      },
      '& a:hover': {
        textDecoration: 'underline',
      },
      '& a.active': {
        textDecoration: 'underline',
      },
      '& :last-child': {
        marginRight: '1rem',
      },
    }
  },
  active: {
    textDecoration: 'underline',
  },
}))

export default useStyles