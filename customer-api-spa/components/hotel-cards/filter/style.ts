import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

const style = makeStyles((theme: AppTheme) => ({
    block: {
      margin: '0.5rem 1rem',
      display: 'flex',
      flexFlow: 'row wrap',
      justifyContent: 'space-between',
      width: 'calc(100% - 2rem)',
    },
    label: {
      display: 'flex',
      alignItems: 'center',
      width: 'calc(100% - 2rem)',
      fontSize: '1rem',
      fontWeight: theme.cssEnv.typo.weightRegular,
      color: theme.cssEnv.palette.menuTextLight,
      cursor: 'pointer',
      outline: 'none',
      userSelect: 'none',
      '& :hover': {
        color: theme.cssEnv.palette.menuText,
      },
    },
    selected: {
      color: theme.cssEnv.palette.menuText,
    },
    toggle: {
      display: 'flex',
      justifyContent: 'center',
      width: '1rem',
      cursor: 'pointer',
      outline: 'none',
      userSelect: 'none',
      '& i': {
        padding: '0.1rem',
        background: 'none',
        borderRadius: '1rem',
        color: theme.cssEnv.palette.menuTextLight,
        outline: 'none',
        userSelect: 'none',
      },
      '& i:hover': {
        color: theme.cssEnv.palette.menuText,
      },
    },
    filter: {
      marginTop: '1rem',
      display: 'flex',
      flexFlow: 'column',
      width: '100%',
    },
    item: {
      display: 'flex',
      justifyContent: 'flex-start',
      alignItems: 'center',
      width: '100%',
    },
    basic: {
      '& span': {
        marginLeft: '1rem',
        fontSize: '1rem',
        fontWeight: theme.cssEnv.typo.weightRegular,
        color: theme.cssEnv.palette.menuTextLight,
      },
    },
    basicSelected: {
      '& span': {
        marginLeft: '1rem',
        fontSize: '1rem',
        fontWeight: theme.cssEnv.typo.weightRegular,
        color: theme.cssEnv.palette.menuText,
      },
    },
  }
))

export default style