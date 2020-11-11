import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  search: {
    display: 'flex',
    flexFlow: 'row',
    justifyContent: 'center',
    paddingTop: '1rem',
    paddingBottom: '1rem',
    marginBottom: '1rem',
    width: '100%',
    backgroundColor: theme.cssEnv.palette.primary,
  },
  grid: {
    display: 'flex',
    flexFlow: 'row wrap',
    '& .filter': {
      display: 'flex',
      marginLeft: '1rem',
      marginRight: '1rem',
      width: '20rem',
    },
    '& .result': {
      display: 'flex',
      flexFlow: 'column',
      justifyContent: 'flex-start',
      alignItems: 'flex-start',
      width: 'calc(100% - calc(20rem + 2.8rem))',
      '& .more': {
        display: 'flex',
        flexFlow: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        width: '100%',
        height: '4rem',
      },
    },
  },
}))

export default useStyles