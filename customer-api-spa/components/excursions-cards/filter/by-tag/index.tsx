import React, { useState } from 'react'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { AppTheme } from '../../../theme'
import { useTheme } from '@material-ui/core'
import useStyles from '../style'
import clsx from 'clsx'
import { SearchExcursionProps } from '../../../../redux/modules/excursion-search/connect'
import { ExcursionTagItem } from '../../../../types/bookings/ExcursionTagItem'

const filterTags: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  if(!props.env){
    return null
  }
  const items = props.env.tags
  const update = props.fn.filterTags
  const selected = props.env.filterParams.tags
  const [open, setOpen] = useState(true)
  const style = useStyles(theme)
  const hasTags = items.length > 0
  return !hasTags ? null : (
    <div className={style.block}>
      <div className={clsx({
        [style.label]: true,
        [style.selected]: open,
      })} onClick={() => setOpen(!open)}>Категории
      </div>
      <div className={style.toggle} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          { open ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }
        </i>
      </div>
      {open && (
        <div className={style.filter}>
          {items.map((x: EnumUI) => (
            <div key={x.id} className={clsx({
              item: true,
              basic: !selected.includes(x.value),
              selected: selected.includes(x.value),
            })}>
              <input
                type="checkbox"
                checked={selected.includes(x.value)}
                value={x.value}
                onChange={({ target }) => {
                  let a = []
                  if (target.checked) {
                    a = [...selected, x.value]
                  } else {
                    a = selected.filter((z: ExcursionTagItem) => z !== x.value)
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
export default filterTags
