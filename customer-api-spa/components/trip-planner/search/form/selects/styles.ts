import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../../../theme'

export const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'center',
    width: '100%',
    height: `calc(1.5rem + 1.5rem)`,
    '& .label': {
      display: 'flex',
      flexBasis: '100%',
      height: '1rem',
      fontWeight: theme.cssEnv.typo.weightLight,
      fontSize: '.7rem',
    },
    '& .input': {
      display: 'flex',
      flexFlow: 'row nowrap',
      justifyContent: 'center',
      alignItems: 'flex-start',
      width: '100%',
      height: '4rem',
      marginLeft: '0.1rem',
      paddingLeft: '0.3rem',
      fontWeight: theme.cssEnv.typo.weightLight,
      cursor: 'pointer',
      userSelect: 'none',

      '& .title': {
        display: 'flex',
        width: '100%',
        fontSize: '0.9rem',
        fontWeight: '500',
      },
      '& .subtitle': {
        fontSize: '0.9rem',
        fontWeight: '400',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
      },
    },
  },
}))

export default useStyles