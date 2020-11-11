import { makeStyles } from '@material-ui/core/styles'

export const useStyles = makeStyles({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    width: '100%',
    backgroundColor: 'white',
    cursor: 'none !important',

    '& .title': {
      display: 'flex',
      width: '100%',
    },

    '& .room': {
      display: 'flex',
      flexFlow: 'row',

      '& .label': {
        display: 'flex',
        width: '100%',
      },
    },
    '& .link': {
      display: 'flex',
      width: '100%',

      '& a': {
        cursor: 'pointer !important',
      },
    },
  },
})

export default useStyles