import React, { useState } from 'react'
import useRootStyles from '../style'
import useFilterStyle from './styles'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

const filterStar: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env){
    return null
  }
  const rootStyles = useRootStyles(theme)
  const filterStyles = useFilterStyle()
  // const items = props.searchPropertyProps.stars
  const update = props.fn.filterStar
  const selected = props.env.filterParams.stars
  const [open, setOpen] = useState(true)
  return (
    <div className={rootStyles.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>По звездности отеля</div>
      <div className={rootStyles.toggle} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          { open ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }
        </i>
      </div>
      {open && (
        <div className={filterStyles.root}>
          <div
            className={clsx({
              star: true,
              selected: selected.length === 0 || selected[0] === 0,
              basic: selected.length === 2 && selected[0] > 0,
            })}
            onClick={() => update([0, 5])}
          >
            0+
          </div>
          <div
            className={clsx({
              star: true,
              selected: selected.length === 0 || selected[0] <= 2,
              basic: selected.length === 2 && selected[0] > 2,
            })}
            onClick={() => update([2, 5])}
          >
            2+
          </div>
          <div
            className={clsx({
              star: true,
              selected: selected.length === 0 || selected[0] <= 3,
              basic: selected.length === 2 && selected[0] > 3,
            })}
            onClick={() => update([3, 5])}
          >
            3+
          </div>
          <div
            className={clsx({
              star: true,
              selected: selected.length === 0 || selected[0] <= 4,
              basic: selected.length === 2 && selected[0] > 4,
            })}
            onClick={() => update([4, 5])}
          >
            4+
          </div>
          <div
            className={clsx({
              star: true,
              selected: selected.length === 0 || selected[1] === 5,
              basic: selected.length === 2 && selected[0] < 5,
            })}
            onClick={() => update([5, 5])}
          >
            5
          </div>
        </div>
      )}
    </div>
  )
}

export default filterStar
