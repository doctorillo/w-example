import { makeStyles } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'

export const useStyles = makeStyles((theme: AppTheme) => {
  const descriptionPadding = '1rem'
  return ({
      root: {
        display: 'flex',
        flexFlow: 'row nowrap',
        marginTop: '.5rem',
        marginBottom: '.5rem',
        width: '100%',
        minWidth: theme.cssEnv.excursionCard.widthHd,
        height: theme.cssEnv.excursionCard.heightHd,
        maxHeight: `calc(${theme.cssEnv.excursionCard.heightHd} - 1rem)`,
        backgroundColor: '#ffffff',
        boxShadow: '0 7px 8px 0 rgba(0, 0, 0, 0.1)',
      },
      photo: {
        width: theme.cssEnv.excursionCard.photo.widthHd,
        '& img': {
          width: theme.cssEnv.excursionCard.photo.widthHd,
          height: `calc(${theme.cssEnv.excursionCard.heightHd} - 1rem)`,
          maxHeight: `calc(${theme.cssEnv.excursionCard.heightHd} - 1rem)`,
        },
      },
      description: {
        display: 'flex',
        flexGrow: 1,
        flexFlow: 'column nowrap',
        paddingLeft: descriptionPadding,
        paddingRight: descriptionPadding,
        width: `calc(100% - ${theme.cssEnv.excursionCard.photo.widthHd})`,
        height: '100%',
        '& .title': {
          width: '100%',
          height: '5.6rem',
          display: 'flex',
          flexFlow: 'row',
          marginTop: '.4rem',
          fontSize: '1.25rem',
          fontWeight: theme.cssEnv.typo.weightRegular,
          color: '#16181b',
          '& a': {
            fontSize: '1.25rem',
            fontWeight: '400',
            color: '#16181b'
          }
        },
        '& .container': {
          display: 'flex',
          flexFlow: 'row wrap',
          justifyContent: 'space-between',
          width: '100%',
          '& .main': {
            display: 'flex',
            flexFlow: 'column wrap',
            width: `calc(100% - ${theme.cssEnv.excursionCard.price.widthHd})`,
            height: `100%`,
            '& .accommodation': {
              width: '100%',
              display: 'flex',
              flexFlow: 'row',
              marginTop: '1rem',
            },
            '& .start': {
              width: '100%',
              display: 'flex',
              flexFlow: 'row',
              marginTop: '1rem',
            },
            '& .tags': {
              width: '100%',
              display: 'flex',
              flexFlow: 'row',
              paddingTop: '.4rem',
            },
            '& .date': {
              display: 'flex',
              justifyContent: 'flex-start',
              width: '100%',
            },
          },
          '& .price': {
            display: 'flex',
            flexFlow: 'column nowrap',
            padding: '.4rem',
            width: `calc(${theme.cssEnv.excursionCard.price.widthHd} - 2rem)`,
            height: theme.cssEnv.excursionCard.price.heightHd,
            '& .amount': {
              display: 'flex',
              justifyContent: 'flex-end',
              width: '100%',
              paddingTop: '1rem',
              paddingBottom: '2rem',
              fontSize: '3rem',
              fontWeight: theme.cssEnv.typo.weightLight,
              color: theme.cssEnv.palette.primary,
            },
            '& .booking': {
              display: 'flex',
              justifyContent: 'flex-end',
              alignSelf: 'flex-end',
              width: '100%',
            },
          }
        }
      }
    }
  )
})

export default useStyles