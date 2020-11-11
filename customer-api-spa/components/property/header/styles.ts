import { AppTheme } from '../../theme'
import { makeStyles } from '@material-ui/core/styles'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    justifyContent: 'flex-start',
    width: '100%',
    height: '5rem',
    backgroundColor: theme.cssEnv.palette.primary,
    '& .header': {
      display: 'flex',
      flexFlow: 'row',
      width: 'calc(100% - 6rem)',
      height: '100%',
      justifyContent: 'center',
      alignItems: 'center',
      '& h3': {
        margin: '0',
        padding: '0',
        display: 'flex',
        fontSize: '3rem',
        fontFamily: theme.cssEnv.typo.family,
        fontWeight: theme.cssEnv.typo.weightLight,
        color: theme.cssEnv.palette.pageBackground,
        lineHeight: '2rem',
      },
      '& .star': {
        paddingLeft: '2rem',
        display: 'flex',
        flexFlow: 'row',
        alignItems: 'center',
      },
    },
  },
}))

export default useStyles