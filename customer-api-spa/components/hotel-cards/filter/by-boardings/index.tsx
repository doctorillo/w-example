import React, { useState } from 'react'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'
import { BoardingUI } from '../../../../types/bookings/BoardingUI'
import useStyles from '../style'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

const filterBoardings = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.env){
    return null
  }
  const items = props.env.boardings
  const update = props.fn.filterBoardings
  const selected = props.env.filterParams.boardings
  const [open, setOpen] = useState(true)
  return (
    <div className={style.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>По питанию и лечению</div>
      <div className={style.toggle} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          { open ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }
        </i>
      </div>
      {open && (
        <div className={style.filter}>
          {items.map((x: BoardingUI) => (
            <div
              key={x.id}
              className={clsx({
                item: true,
                basic: !selected.includes(x.id),
                selected: selected.includes(x.id),
              })}
            >
              <input
                type="checkbox"
                checked={selected.includes(x.id)}
                value={x.id}
                onChange={({ target }) => {
                  let a = []
                  if (target.checked) {
                    a = [...selected, x.id]
                  } else {
                    a = selected.filter((z: string) => z !== x.id)
                  }
                  update(a)
                }}
              />
              {x.withTreatment ? <span>Лечение {x.label.label.toLowerCase()}</span> : <span>{x.label.label}</span>}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default filterBoardings
