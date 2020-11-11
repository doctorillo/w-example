import React from 'react'
import useRootStyles from '../style'
import useStyles from './styles'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'

const filterTopInfo: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env){
    return null
  }
  const rootStyle = useRootStyles(theme)
  const style = useStyles(theme)
  const filteredCount = props.env.filteredCount
  const allCount = props.env.allCount
  const filterReset = props.fn.filterReset
  const filterStop = props.fn.filterStop
  const viewStop = props.env.filterParams.viewStop
  return (
    <div className={rootStyle.block}>
      <div className={'label'}>
        Показано {filteredCount}/{allCount} отелей
      </div>
      <div className={'filter'}>
        <div
          className={clsx({
            item: true,
            basic: !viewStop,
            selected: viewStop,
          })}
        >
          <input
            type="checkbox"
            checked={viewStop}
            onChange={() => {
              filterStop(!viewStop)
            }}
          />
          <span>Показывать остановленные продажи</span>
        </div>
      </div>
      <div className={clsx({
        disabled: filteredCount === allCount
      })}>
        <button
          className={style.button}
          onClick={filterReset}
        >
          Сбросить все фильтры
        </button>
      </div>
    </div>
  )
}

export default filterTopInfo
