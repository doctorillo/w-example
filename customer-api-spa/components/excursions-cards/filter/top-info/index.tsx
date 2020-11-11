import React from 'react'
import useRootStyles from '../style'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { SearchExcursionProps } from '../../../../redux/modules/excursion-search/connect'
import clsx from 'clsx'

const filterTopInfo: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  if (!props.env){
    return null
  }
  const styles = useRootStyles(theme)
  const {env: { filteredCount, allCount }, fn: { filterReset }} = props
  const active = filteredCount !== allCount
  return (
    <div className={styles.block}>
      <div className={'label'}>
        Показано {filteredCount}/{allCount} экскурсий
      </div>
      <div className={styles.buttonBlock}>
        <button
          className={clsx({
            [styles.button]: true,
            [styles.primaryButton]: active,
            [styles.disabledButton]: !active,
          })}
          disabled={!active}
          onClick={filterReset}
        >
          Сбросить все фильтры
        </button>
      </div>
    </div>
  )
}

export default filterTopInfo
