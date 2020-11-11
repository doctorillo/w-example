import React, { useState } from 'react'
import { AppTheme } from '../../../theme'
import { useTheme } from '@material-ui/core'
import useStyles from '../style'
import clsx from 'clsx'
import { SearchExcursionProps } from '../../../../redux/modules/excursion-search/connect'
import { formatShortLocalDate, parseLocalDate } from '../../../../types/DateRange'
import { DateString } from '../../../../types/basic/DateString'

const filterDates: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env) {
    return null
  }
  const items = props.env.excursionDates
  const update = props.fn.filterDates
  const selected = props.env.filterParams.viewDates
  const [open, setOpen] = useState(true)
  const style = useStyles(theme)
  return (
    <div className={style.block}>
      <div className={clsx({
        [style.label]: true,
        [style.selected]: open,
      })} onClick={() => setOpen(!open)}>
        Даты проведения
      </div>
      <div className={style.toggle} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          {open ? 'keyboard_arrow_up' : 'keyboard_arrow_down'}
        </i>
      </div>
      {open && (
        <div className={style.filter}>
          {items.map((x: DateString) => {
            const date = parseLocalDate(x)
            return (
              <div key={x} className={clsx({
                item: true,
                basic: !selected.includes(x),
                selected: selected.includes(x),
              })}>
                <input
                  type="checkbox"
                  checked={selected.includes(x)}
                  value={x}
                  onChange={({ target }) => {
                    let a = []
                    if (target.checked) {
                      a = [...selected, x]
                    } else {
                      a = selected.filter(z => z !== x)
                    }
                    update(a)
                  }}
                />
                <span>{formatShortLocalDate(date)}</span>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
export default filterDates
