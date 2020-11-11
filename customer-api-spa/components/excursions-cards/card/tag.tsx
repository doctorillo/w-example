import React from 'react'
import { makeStyles, useTheme } from '@material-ui/core/styles'
import { AppTheme } from '../../theme'
import { ExcursionTagItem } from '../../../types/bookings/ExcursionTagItem'

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: 'flex',
    padding: '.5rem',
    marginLeft: '.5rem',
    fontSize: '.8rem',
    color: theme.cssEnv.palette.menuTextLight,
    border: `1px solid ${theme.cssEnv.palette.menuTextLight}`,
    borderRadius: '3px',
  }
}))

const tagItem: React.FC<{tag: ExcursionTagItem}> = (props: {tag: ExcursionTagItem}) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  let lable = ''
  switch (props.tag) {
    case ExcursionTagItem.Walk:
      lable = 'Пешеходная'
      break;
    case ExcursionTagItem.Bus:
      lable = 'Автобусная'
      break;
    case ExcursionTagItem.River:
      lable = 'Речная'
      break;
    case ExcursionTagItem.Evening:
      lable = 'Вечерняя'
      break;
    case ExcursionTagItem.Snacks:
      lable = 'С едой'
      break;
    case ExcursionTagItem.TwoDays:
      lable = '2-х дневная'
      break;
    case ExcursionTagItem.ThreeDays:
      lable = '3-х дневная'
      break;
    case ExcursionTagItem.FourDays:
      lable = '4-х дневная'
      break;
  }
  return <div className={style.root}>
    {lable}
  </div>
}

export default tagItem