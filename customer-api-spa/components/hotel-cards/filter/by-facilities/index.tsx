import React, { useState } from 'react'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'
import { EnumUI } from '../../../../types/basic/EnumUI'
import useStyles from '../style'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

const filterFacilities = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  if (!props.env){
    return null
  }
  const items = props.env.facilities
  const update = props.fn.filterFacilities
  const selected = props.env.filterParams.facilities
  const [open, setOpen] = useState(true)
  return (
    <div className={style.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>В номерах</div>
      <div className={style.toggle} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          { open ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }
        </i>
      </div>
      {open && (
        <div className={style.filter}>
          {items.map((x: EnumUI) => (
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
              <span>{x.label.label}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default filterFacilities
