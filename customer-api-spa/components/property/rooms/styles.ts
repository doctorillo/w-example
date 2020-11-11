import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    justifyContent: 'flex-start',
    width: '100%',
    marginLeft: '2rem',
    marginRight: '2rem',
    '& .header': {
      display: 'flex',
      flexFlow: 'row',
      width: 'calc(100% - 6rem)',
      justifyContent: 'flex-start',
      alignContent: 'center',
      '& h3': {
        margin: '0',
        padding: '0',
        display: 'flex',
        fontSize: '1.8rem',
        fontFamily: theme.cssEnv.typo.family,
        fontWeight: theme.cssEnv.typo.weightLight,
        color: theme.cssEnv.palette.secondaryLight,
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