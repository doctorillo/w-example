import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    '& .header': {
      display: 'flex',
      flexFlow: 'row',
      color: theme.cssEnv.palette.menuText,
      '& .title': {
        width: '70%',
        fontSize: '2rem',
        fontWeight: '400',
      },
      '& .date': {
        width: '30%',
      },
    },
    '& .total': {
      display: 'flex',
      alignItems: 'baseline',
      width: '100%',
      fontSize: '2rem',
      color: theme.cssEnv.palette.primary,
      fontWeight: theme.cssEnv.typo.weightRegular,
      paddingBottom: '1rem',
      '& .label': {
        fontSize: '2.5rem',
        fontWeight: theme.cssEnv.typo.weightLight,
        color: theme.cssEnv.palette.menuTextExtraLight,
        paddingRight: '1rem',
      },
    },
  },
}))

export default useStyles