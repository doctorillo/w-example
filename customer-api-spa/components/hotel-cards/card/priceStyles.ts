import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: `calc(${theme.cssEnv.propertyCard.price.widthHd} - .5rem)`,
    height: 'calc(100% - 1.4rem)',
    border: `solid 1px ${theme.cssEnv.palette.menuTextExtraLight}`,
    marginTop: '.2rem',
    marginBottom: '.2rem',
    marginRight: '.2rem',
    padding: '.5rem',
    cursor: 'pointer',
    '&:hover': {
      boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
    },
    '& .title': {
      display: 'flex',
      justifyContent: 'flex-end',
      width: '100%',
      fontSize: '0.75rem',
      fontWeight: 500,
      color: theme.cssEnv.palette.menuTextLight,
    },
    '& .price': {
      display: 'flex',
      flexFlow: 'row nowrap',
      justifyContent: 'space-between',
      width: '100%',
      '& .from': {
        display: 'flex',
        flexGrow: 1,
        justifyContent: 'flex-end',
        alignItems: 'flex-end',
        width: '1.5rem',
        paddingBottom: '.5rem',
        fontSize: '1rem',
        color: theme.cssEnv.palette.primary,
      },
      '& .from-stop': {
        display: 'flex',
        flexGrow: 1,
        justifyContent: 'flex-end',
        alignItems: 'flex-end',
        width: '1rem',
        paddingBottom: '.5rem',
        fontSize: '1rem',
        color: theme.cssEnv.palette.menuTextLight,
      },
      '& .amount': {
        display: 'flex',
        alignSelf: 'flex-end',
        fontSize: '2rem',
        color: theme.cssEnv.palette.primary,
      },
      '& .amount-stop': {
        display: 'flex',
        alignSelf: 'flex-end',
        fontSize: '2rem',
        color: theme.cssEnv.palette.menuTextLight,
      },
    },
    '& .discount': {
      display: 'flex',
      alignSelf: 'flex-end',
      fontSize: '1rem',
      color: theme.cssEnv.palette.primary,
    },
    '& .room': {
      display: 'flex',
      justifyContent: 'flex-end',
      width: '100%',
      fontSize: '0.75rem',
      fontWeight: '500',
      color: theme.cssEnv.palette.menuTextLight,
    },
    '& .boarding': {
      display: 'flex',
      justifyContent: 'flex-end',
      alignItems: 'center',
      width: '100%',
      fontSize: '0.75rem',
      fontWeight: 500,
      color: theme.cssEnv.palette.menuTextLight,
    },
  },
}))

export default useStyles