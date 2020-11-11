import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  navbar: {
    position: 'fixed',
    display: 'flex',
    flexFlow: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    backgroundColor: 'white',
    borderBottom: `1px solid ${theme.cssEnv.palette.textLight}`,
    boxShadow: '0 7px 10px 0 rgba(0, 0, 0, 0.1)',
    width: '100%',
    height: '5rem',
    zIndex: 10,
    '& .logo': {
      display: 'flex',
      width: '5rem',
      justifyContent: 'center',
      alignItems: 'baseline',
      cursor: 'pointer',
      '& .title': {
        fontSize: `calc(${theme.cssEnv.typo.size} * 4)`,
        color: theme.cssEnv.palette.primary,
        fontWeight: theme.cssEnv.typo.weightRegular,
      },
      '& .subtitle': {
        fontSize: `calc(${theme.cssEnv.typo.size} * 2)`,
        color: theme.cssEnv.palette.secondary,
        fontWeight: theme.cssEnv.typo.weightLight,
      },
    },
    '& .menu': {
      display: 'flex',
      width: 'calc(100% - 30rem)',
      height: '100%',
      borderRight: theme.cssEnv.palette.pageBackgroundDarker,
    },
    '& .call': {
      display: 'flex',
      justifyContent: 'flex-start',
      alignItems: 'center',
      width: '14rem',
      height: '100%',
      marginLeft: '1rem',
      padding: '0',
      '& :last-child': {
        marginLeft: '1rem',
      },
    },
    '& .mail': {
      display: 'flex',
      alignItems: 'center',
      width: '13rem',
      height: '100%',
      marginLeft: '1rem',
      borderRight: `1px solid ${theme.cssEnv.palette.pageBackgroundDarker}`,
      '& :last-child': {
        marginLeft: '1rem',
        marginRight: '1rem',
      },
    },
    '& .burger': {
      display: 'none',
      height: '100%',
      right: `calc(${theme.cssEnv.typo.size} * 3)`,
      cursor: 'pointer',
    },
    '& .shopping': {
      display: 'flex',
      flexGrow: '1',
      alignItems: 'center',
      justifyContent: 'flex-end',
      height: '100%',
      '& :first-child': {
        marginLeft: '1rem',
      },
      '& :last-child': {
        marginRight: '1rem',
      },
    },
  },
  icon: {
    fontSize: '1.5rem',
    color: theme.cssEnv.palette.primary,
  },
}))

export default useStyles