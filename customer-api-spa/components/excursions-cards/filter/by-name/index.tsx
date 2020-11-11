import React, { useState } from 'react'
import { useDebouncedCallback } from 'use-debounce'
import { AppTheme } from '../../../theme'
import { makeStyles, useTheme } from '@material-ui/core/styles'
import useStyles from '../style'
import clsx from 'clsx'
import { SearchExcursionProps } from '../../../../redux/modules/excursion-search/connect'

const s = makeStyles((theme: AppTheme) => ({
  filter: {
    marginTop: '1rem',
    '& input': {
      width: '100%',
      lineHeight: '2rem',
      fontSize: '1rem',
      color: theme.cssEnv.palette.textLight,
    },
  },
}))

const filterByName: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env) {
    return null
  }
  const { env: { filterParams: { name } }, fn: { filterName }} = props
  const rootStyle = useStyles(theme)
  const style = s(theme)
  const [open, setOpen] = useState(true)
  const [debouncedCallback] = useDebouncedCallback(
    value => {
      filterName(value)
    },
    1000,
  )
  return (
    <div className={rootStyle.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>По названию экскурсии
      </div>
      <div className={'toggle'} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          {open ? 'keyboard_arrow_up' : 'keyboard_arrow_down'}
        </i>
      </div>
      {open && (
        <div className={style.filter}>
          <input
            defaultValue={name || ''}
            onChange={e => debouncedCallback(e.target.value)}
          />
        </div>
      )}
    </div>
  )
}

export default filterByName
