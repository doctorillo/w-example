import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    width: '100%',
    '& .title': {
      paddingTop: '1rem',
      paddingBottom: '1rem',
      color: theme.cssEnv.palette.primary,
      fontSize: '2rem',
      fontWeight: theme.cssEnv.typo.weightLight,
    },
    '& .excursion-container': {
      display: 'flex',
      flexFlow: 'row wrap',
      width: '100%',
      marginBottom: '1rem',
      '& .idx-container': {
        display: 'flex',
        alignSelf: 'center',
        width: '1.5rem',
        height: '100%',
        fontSize: '1.4rem',
        fontWeight: theme.cssEnv.typo.weightLight,
        color: theme.cssEnv.palette.menuTextLight,
      },
      '& .body-container': {
        display: 'flex',
        flexFlow: 'column wrap',
        width: 'calc(100% - 1.5rem)',
        '& .name': {
          color: theme.cssEnv.palette.menuText,
          fontSize: '1rem',
          fontWeight: theme.cssEnv.typo.weightMedium,
          '& .date': {
            fontSize: '.9rem',
            color: theme.cssEnv.palette.menuTextLight,
          }
        },
        '& .description': {
          display: 'flex',
          flexFlow: 'row wrap',
          width: '100%',
          '& .guest': {
            display: 'flex',
            width: '7rem',
          },
          '& .property': {
            display: 'flex',
            width: '20rem',
          },
        },
        '& .price': {
          display: 'flex',
          width: '20rem',
          color: theme.cssEnv.palette.menuText,
          fontSize: '.9rem',
          fontWeight: theme.cssEnv.typo.weightMedium,
        },
      },
    },
  },
  icon: {
    display: 'flex',
    width: '3rem'
  },
  content: {
    display: 'flex',
    width: 'calc(100% - 3rem)',
    fontSize: '.9rem',
    color: theme.cssEnv.palette.menuTextLight,
    fontWeight: theme.cssEnv.typo.weightMedium
  }
}))

export default useStyles