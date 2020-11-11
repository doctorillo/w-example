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
    buttonBlock: {
      width: '100%',
    },
    primaryButton: {
      backgroundColor: theme.cssEnv.palette.primary,
      color: '#fff',
    },
    disabledButton: {
      backgroundColor: theme.cssEnv.palette.menuTextExtraLight,
      color: '#fff',
    },
    button: {
      display: 'block',
      width: '100%',
      height: '2rem',
      marginTop: '1.75rem',
      fontWeight: theme.cssEnv.typo.weightLight,
      fontSize: '1rem',
      border: 'none',
      cursor: 'pointer',
      '&:active': {
        outline: '0',
      },
      '&:focus': {
        outline: '0',
      },
    },
  }
))

export default style