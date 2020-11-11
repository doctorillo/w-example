import React, { useEffect, useRef, useState } from 'react'
import useStyles from '../style'
import useFilterStyles from './styles'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import clsx from 'clsx'
import { SearchExcursionProps } from '../../../../redux/modules/excursion-search/connect'

const useFilterByPrice: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env){
    return null
  }
  const rootStyle = useStyles(theme)
  const filterStyle = useFilterStyles(theme)
  const items = props.env.price
  const update = props.fn.filterPrice
  const selected = props.env.filterParams.price
  const pMin = items[0]
  const pMax = items[1]
  const vMin = selected[0] || 0
  const vMax = selected[1] || 0
  const [open, setOpen] = useState(true)
  const minRef = useRef<HTMLInputElement>(null)
  const maxRef = useRef<HTMLInputElement>(null)
  const [minState, setMinState] = useState(vMin)
  const [maxState, setMaxState] = useState(vMax)
  useEffect(() => {
    if (minRef?.current && pMin === vMin) {
      minRef.current.value = vMin.toString()
      setMinState(vMin)
    }
    if (maxRef?.current && pMax === vMax) {
      maxRef.current.value = vMax.toString()
      setMaxState(vMax)
    }
  }, [vMin, vMax, pMin, pMax])
  return (
    <div className={rootStyle.block}>
      <div className={clsx({
        label: true,
        selected: open,
      })} onClick={() => setOpen(!open)}>По цене</div>
      <div className={rootStyle.toggle} onClick={() => setOpen(!open)}>
        <i className={`material-icons`}>
          { open ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }
        </i>
      </div>
      {open && (
        <div className={filterStyle.filter}>
          <div className={'label'}>
            от
          </div>
          <div className={'amount'}>
            <input
              ref={minRef}
              type="number"
              value={minState}
              onChange={(e) => setMinState(parseInt(e.target.value))}/>
          </div>
          <div className={'label'}>
            до
          </div>
          <div className={'amount'}>
            <input
              ref={maxRef}
              type="number"
              value={maxState}
              onChange={(e) => setMaxState(parseInt(e.target.value))}/>
          </div>
          <div className={rootStyle.buttonBlock}>
            <button
              className={clsx({
                [rootStyle.button]: true,
                [rootStyle.primaryButton]: !(vMin === minState && vMax === maxState),
                [rootStyle.disabledButton]: vMin === minState && vMax === maxState,
              })}
              disabled={vMin === minState && vMax === maxState}
              onClick={() => update([minState, maxState])}
            >
              Применить
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default useFilterByPrice