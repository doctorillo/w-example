import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    width: '100%',
    maxWidth: '40rem',
    '& .title': {
      paddingTop: '1rem',
      paddingBottom: '1rem',
      color: theme.cssEnv.palette.primary,
      fontSize: '2rem',
      fontWeight: theme.cssEnv.typo.weightLight,
    },
    '& .property-container': {
      display: 'flex',
      flexFlow: 'row wrap',
      width: '100%',
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
          justifyContent: 'space-between',
          alignItems: 'center',
          width: '100%',
          '& .moon': {
            display: 'flex',
            width: '7rem',
            justifyContent: 'space-between',
            alignItems: 'center',
          },
          '& .guest': {
            display: 'flex',
            width: '7rem',
          },
          '& .room': {
            display: 'flex',
          },
          '& .eating': {
            display: 'flex',
          },
        },
        '& .price': {
          display: 'flex',
          color: theme.cssEnv.palette.menuText,
          fontSize: '1rem',
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
    justifyContent: 'space-between',
    alignItems: 'center',
    width: 'calc(100% - 3rem)',
    fontSize: '.9rem',
    color: theme.cssEnv.palette.menuTextLight,
    fontWeight: theme.cssEnv.typo.weightMedium
  }
}))

export default useStyles