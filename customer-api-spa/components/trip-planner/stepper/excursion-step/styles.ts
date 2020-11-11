import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'row wrap',
    width: '100%',
    '& .header': {
      display: 'flex',
      width: '100%',
      fontSize: '2rem',
      color: theme.cssEnv.palette.primary,
      borderTop: `1px solid ${theme.cssEnv.palette.menuTextExtraLight}`,
    },
    '& .container': {
      display: 'flex',
      width: '100%',
      borderTop: `1px solid ${theme.cssEnv.palette.menuTextExtraLight}`,
    },
    '& .count': {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      width: '3rem',
      '& .counter': {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        width: '2rem',
        height: '2rem',
        fontSize: '1.5rem',
        color: '#ffffff',
        backgroundColor: theme.cssEnv.palette.menuTextExtraLight,
        borderRadius: '50%',
      },
      '& .active': {
        backgroundColor: theme.cssEnv.palette.secondary,
      },
    },
    '& .data': {
      display: 'flex',
      flexFlow: 'row wrap',
      padding: '.5rem',
      width: 'calc(calc(100% - calc(3rem + 8rem)) - 1rem)',
      '& .title': {
        display: 'flex',
        width: '100%',
        color: theme.cssEnv.palette.secondary,
        fontWeight: theme.cssEnv.typo.weightMedium,
      },
      '& .description, & .price': {
        display: 'flex',
        flexFlow: 'column',
        width: 'calc(100% / 2)',
        '& .item': {
          display: 'flex',
          flexFlow: 'row',
          width: '100%',
          marginTop: '1rem',
          '& .icon': {
            display: 'flex',
            width: '2rem',
          },
          '& .content': {
            display: 'flex',
            flexFlow: 'row wrap',
            width: 'calc(100% - 2rem)',
            '& .label': {
              display: 'flex',
              width: 'calc(100% / 2)',
            },
            '& .value': {
              display: 'flex',
              width: 'calc(100% / 2)',
            },
          },
        },
      },
    },
    '& .action': {
      display: 'flex',
      flexFlow: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      width: '24rem',
      '& .total': {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        width: '100%',
        fontSize: '2.5rem',
        fontWeight: '400',
        color: theme.cssEnv.palette.menuTextExtraLight,
      },
      '& .active': {
        color: theme.cssEnv.palette.primary,
      },
      '& .button': {
        display: 'flex',
        width: '100%',
      },
    },
  },
}))

export default useStyles