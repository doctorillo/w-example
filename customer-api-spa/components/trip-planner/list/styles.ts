import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    top: '10rem',
    height: 'calc(100% - 11rem)',
    margin: '1rem',
    '& .form': {
      display: 'flex',
      flexFlow: 'row',
      alignItems: 'center',
      width: 'calc(100% - 8rem)',
      height: theme.cssEnv.menu.heightHd,
      backgroundColor: '#ffffff',
      boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
      padding: '1rem',
      '& .city': {
        display: 'flex',
        flexFlow: 'row nowrap',
        justifyContent: 'space-between',
        width: '8.6rem',
        minWidth: '8.6rem',
        paddingRight: '0.1rem',
      },
      '& .partHalf': {
        display: 'flex',
        flexFlow: 'row nowrap',
        justifyContent: 'space-between',
        width: 'calc(calc(100% - calc(8.6rem + 10rem)) / 2)',
        paddingLeft: '0.1rem',
      },
      '& .partHalf::last-child': {
        paddingLeft: '0.5rem',
        paddingRight:
          '0.5rem',
      },
      '& .btn': {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        width: '10rem',
        flexShrink: 1,
        marginLeft: '0.1rem',
        paddingTop: '1.5rem',
        '& button': {
          width: '100%',
          height: '2.45rem',
          backgroundColor: theme.cssEnv.palette.accent,
          color: theme.cssEnv.palette.menuText,
          fontWeight: theme.cssEnv.typo.weightMedium,
          fontSize: '1rem',
          border: 'none',
          cursor: 'pointer',
          '&:active': {
            outline: 0,
          },
          '&:focus': {
            outline: 0,
          },
        },
      },
    },
  },
}))

export default useStyles