import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  filter: {
    display: 'flex',
    marginTop: '1rem',
    width: '100%',
    flexFlow: 'row wrap',
    justifyContent: 'space-between',
    alignContent: 'center',

    '& .label': {
      width: '10%',
      lineHeight: '2rem',
      fontSize: '1rem',
      color: theme.cssEnv.palette.textLight,
    },
    '& .amount': {
      width: '40%',
      '& input': {
        width: '80%',
        lineHeight: '2rem',
        fontSize: '1rem',
        color: theme.cssEnv.palette.menuTextLight,
        outline: 'none',
        userSelect: 'none',
      },
    },
    '& .buttonBlock': {
      width: '100%',
      '& .button': {
        display: 'block',
        width: '100%',
        height: '2rem',
        marginTop: '1.75rem',
        backgroundColor: theme.cssEnv.palette.primary,
        color: '#fff',
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
    },
  },
}))

export default useStyles