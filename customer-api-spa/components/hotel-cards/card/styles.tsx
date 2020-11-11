import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'row',
    width: '100%',
    marginTop: '.5rem',
    marginBottom: '.5rem',
    minWidth: theme.cssEnv.propertyCard.widthHd,
    height: theme.cssEnv.propertyCard.heightHd,
    maxHeight: `calc(${theme.cssEnv.propertyCard.heightHd} - 1rem)`,
    backgroundColor: '#ffffff',
    boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
    '& :first-child': {
      marginTop: 0,
    },
    '& .photo': {
      width: theme.cssEnv.propertyCard.photo.widthHd,
      '& img': {
        width: theme.cssEnv.propertyCard.photo.widthHd,
        height: `calc(${theme.cssEnv.propertyCard.heightHd} - 1rem)`,
        maxHeight: `calc(${theme.cssEnv.propertyCard.heightHd} - 1rem)`,
      },
    },
    '& .description': {
      display: 'flex',
      flexFlow: 'row nowrap',
      width: `calc(100% - calc(${theme.cssEnv.propertyCard.photo.widthHd} - 1rem))`,
      '& .main': {
        display: 'flex',
        flexFlow: 'column',
        width: `calc(100% - calc(${theme.cssEnv.propertyCard.photo.widthHd} - 2rem))`,
        '& .top': {
          display: 'flex',
          flexFlow: 'row',
          marginTop: '1rem',
          marginLeft: '2.25rem',
        },
        '& .title': {
          display: 'flex',
          flexFlow: 'row',
          marginTop: '1rem',
          marginLeft: '2.25rem',
          fontSize: '1.25rem',
          fontWeight: '400',
          color: '#16181b',
        },
        '& .address': {
          display: 'flex',
          flexFlow: 'row',
          marginTop: '1rem',
          marginLeft: '2.25rem',
          fontSize: '1rem',
          fontWeight: '400',
          color: '#b9c0cd',
        },
      },
    },
  },
}))

export default useStyles