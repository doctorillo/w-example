import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'row wrap',
    alignItems: 'center',
    justifyContent: 'space-between',
    width: 'calc(100% - 2rem)',
    height: `calc(${theme.cssEnv.menu.heightHd} - 1rem)`,
    backgroundColor: '#ffffff',
    boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
    paddingLeft: '1rem',
    paddingRight: '1rem',
    '& .header': {
      display: 'flex',
      flexFlow: 'row nowrap',
      width: '100%',
      height: '1.2rem',
      justifyContent: 'flex-end',
    },
    '& .close': {
      display: 'flex',
      flexGrow: 1,
      width: '1rem',
    },
    '& .agent': {
      display: 'flex',
      flexFlow: 'row nowrap',
      width: '12rem',
      minWidth: '12rem',
      maxWidth: '12rem',
      height: '5rem',
    },
    '& .city': {
      display: 'flex',
      flexFlow: 'row nowrap',
      marginLeft: '0.5rem',
      width: 'calc(12rem - 0.5rem)',
      minWidth: 'calc(12rem - 0.5rem)',
      maxWidth: 'calc(12rem - 0.5rem)',
      height: '5rem',
      /*backgroundColor: 'aqua',*/
    },
    '& .dates': {
      display: 'flex',
      flexFlow: 'row nowrap',
      justifyContent: 'space-between',
      marginLeft: '0.5rem',
      width: 'calc(14rem - 0.5rem)',
      minWidth: 'calc(14rem - 0.5rem)',
      maxWidth: 'calc(14rem - 0.5rem)',
      height: '4rem',
      /*backgroundColor: 'navajowhite',*/
    },
    '& .person': {
      display: 'flex',
      flexFlow: 'row nowrap',
      width: '12rem',
      minWidth: '12rem',
      maxWidth: '12rem',
      height: '4rem',
      /*backgroundColor: 'lavender',*/
    },
    '& .btn': {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'baseline',
      width: 'calc(10rem - 0.1rem)',
      minWidth: '10rem',
      flexShrink: '1',
      marginLeft: '0.1rem',
      paddingTop: '1rem',
      height: '4rem',
      /*'& button': {
        width: '100%',
        height: 'calc(4rem - 1.5rem)',
        backgroundColor: theme.cssEnv.palette.accent,
        color: theme.cssEnv.palette.menuText,
        fontWeight: theme.cssEnv.typo.weightMedium,
        fontSize: '1rem',
        border: 'none',
        cursor: 'pointer',
        '&:active': {
          outline: '0',
        },
        '&:focus': {
          outline: '0',
        },
      },*/
    },
  },
}))

export default useStyles