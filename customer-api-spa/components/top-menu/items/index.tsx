import React from 'react'
import Link from 'next/link'
import useStyles from './styles'
import { NavigationContext } from '../../../types/contexts/NavigationContext'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'
import clsx from 'clsx'

export interface TopMenuItemProps {
  ctx: NavigationContext;
}

const topMenuItem = (props: TopMenuItemProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { ctx } = props
  const viewBuilder = ctx === NavigationContext.Builder
  const viewHotels = ctx === NavigationContext.Hotels
  const viewExcursions = ctx === NavigationContext.Excursions
  return (
    <ul className={style.root}>
      <li>
        <Link href='/search'>
          <a href="#" className={clsx({
            active: viewBuilder,
          })}>Конструктор путешествия</a>
        </Link>
      </li>
      { (viewHotels || viewExcursions) && ([<li key={'m_hotel'}>
        <Link href='/hotels'>
          <a href="#" className={clsx({
            active: viewHotels,
          })}>Отели</a>
        </Link>
      </li>, <li key={'m_excursion'}>
        <Link href='/excursions'>
          <a href="#" className={clsx({
            active: viewExcursions,
          })}>Экскурсии</a>
        </Link>
      </li>])}
    </ul>
  )
}

export default topMenuItem
