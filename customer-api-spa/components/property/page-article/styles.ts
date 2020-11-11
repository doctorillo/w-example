import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    width: 'calc(100% - 6rem)',
    marginLeft: '3rem',
    marginRight: '3rem',
    justifyContent: 'center',
    alignContent: 'center',

    '& .header': {
      display: 'flex',
      width: '100%',
      fontSize: '1.8rem',
      fontFamily: theme.cssEnv.typo.family,
      fontWeight: theme.cssEnv.typo.weightLight,
      color: theme.cssEnv.palette.secondaryLight,
      lineHeight: '1rem',
    },
    '& .text': {
      display: 'flex',
      flexFlow: 'row wrap',
      width: '100%',
      backgroundColor: 'white',
      padding: '1rem',
      boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',

      '& p': {
        display: 'flex',
        width: '100%',
      },
    },
  },
}))

export default useStyles