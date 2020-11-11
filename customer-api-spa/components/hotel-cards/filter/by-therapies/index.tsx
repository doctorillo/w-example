import React, { useState } from 'react'
import useStyles from '../style'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'
import { EnumUI } from '../../../../types/basic/EnumUI'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

const filterTherapies: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env){
    return null
  }
  const style = useStyles(theme)
  const items = props.env.therapies
  const update = props.fn.filterTherapies
  const selected = props.env.filterParams.therapies
  const [open, setOpen] = useState(true)
  return (
    <div className={style.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>Терапия</div>
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

export default filterTherapies
