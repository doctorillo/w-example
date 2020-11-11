import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    justifyContent: 'flex-start',
    width: '100%',
    '& .header': {
      display: 'flex',
      flexFlow: 'row',
      width: 'calc(100% - 6rem)',
      justifyContent: 'center',
      alignContent: 'center',
      '& h3': {
        margin: '0',
        padding: '0',
        display: 'flex',
        fontSize: '3rem',
        fontFamily: theme.cssEnv.typo.family,
        fontWeight: theme.cssEnv.typo.weightLight,
        color: theme.cssEnv.palette.text,
        lineHeight: '2rem',
      },
      '& .star': {
        marginTop: '2rem',
        paddingLeft: '2rem',
        display: 'flex',
      },
    },
  },
}))

export default useStyles