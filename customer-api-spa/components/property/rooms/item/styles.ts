import { makeStyles } from '@material-ui/core/styles'

export const useStyles = makeStyles({
  root: {
    display: 'flex',
    flexFlow: 'row',
    width: 'calc(100% - 4rem)',
    height: '11rem',
    marginTop: '0.5rem',
    backgroundColor: '#ffffff',
    boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
    '& .photo': {
      width: '16rem',

      '& img': {
        width: '16rem',
        height: '11rem',
      },
    },
    '& .description': {
      display: 'flex',
      flexFlow: 'row nowrap',
      width: 'calc(100% - 16rem)',

      '& .main': {
        display: 'flex',
        flexFlow: 'column',
        justifyContent: 'space-around',
        width: 'calc(100% - 16rem)',
        paddingLeft: '1rem',

        '& .title': {
          display: 'flex',
          flexFlow: 'row',
          marginTop: '1rem',
          fontSize: '1.25rem',
          fontWeight: '400',
          color: '#16181b',
        },
        '& .subtitle': {
          display: 'flex',
          flexFlow: 'row',
          marginTop: '1rem',
          marginBottom: '1rem',
          fontSize: '1rem',
          fontWeight: '400',
          color: '#b9c0cd',
        },
        '& .info': {
          display: 'flex',
          flexFlow: 'row wrap',
          width: '100%',

          '& .item': {
            display: 'flex',
            flexFlow: 'row nowrap',
            width: '50%',

            '& .icon': {
              display: 'flex',
              width: '2rem',
              fontSize: '1rem',
            },
            '& .label': {
              display: 'flex',
              width: 'calc(100% - 2rem)',
              justifyContent: 'flex-start',
              alignItems: 'center',
              fontSize: '1rem',
              fontWeight: '400',
              color: '#b9c0cd',
            },
          },
        },
      },
      '& .price': {
        display: 'flex',
        flexFlow: 'column',
        width: '16rem',
        justifyContent: 'center',
        alignItems: 'center',
      },
    },
  },
})

export default useStyles