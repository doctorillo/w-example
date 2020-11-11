import React, { useState } from 'react'
import { useDebouncedCallback } from 'use-debounce'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'
import { AppTheme } from '../../../theme'
import { makeStyles, useTheme } from '@material-ui/core/styles'
import useStyles from '../style'
import clsx from 'clsx'

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

const filterByName: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env) {
    return null
  }
  const rootStyle = useStyles(theme)
  const style = s(theme)
  const update = props.fn.filterName
  const selected = props.env.filterParams.name
  const [open, setOpen] = useState(true)
  const [debouncedCallback] = useDebouncedCallback(
    value => {
      update(value)
    },
    1000,
  )
  return (
    <div className={rootStyle.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>По названию отеля
      </div>
      <div className={'toggle'} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          {open ? 'keyboard_arrow_up' : 'keyboard_arrow_down'}
        </i>
      </div>
      {open && (
        <div className={style.filter}>
          <input
            defaultValue={selected || ''}
            onChange={e => debouncedCallback(e.target.value)}
          />
        </div>
      )}
    </div>
  )
}

export default filterByName
