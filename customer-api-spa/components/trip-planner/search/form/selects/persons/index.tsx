import React, { useRef, useState } from 'react'
import useStyles from '../styles'
import Select from './select'
import { groupLabel, QueryGroup } from '../../../../../../types/bookings/QueryGroup'
import { LangItem } from '../../../../../../types/LangItem'
import { GroupLabel } from '../../../../../../types/parties/PersonGroup'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../../theme'

const rect = (ref: any)  => ref.current.getBoundingClientRect()

const usePersonSelect = (props: { queryGroup: QueryGroup; update(group: QueryGroup): void }) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { queryGroup, update } = props
  const inputEl = useRef(null)
  const [open, setOpen] = useState(false)
  const label: GroupLabel = groupLabel(queryGroup, LangItem.Ru) //plannerProperty && makeLabel(plannerClients, plannerProperty, lang)
  return (
    <div className={style.root}>
      <div className={'label'}>В составе</div>
      {/*<div className={style.icon}>
        <GuestsIcon
          onClick={() => setOpen(!open)}
          viewBox={'0 0 100 100'}
          width={'26px'}
          height={'26px'}
          fill={'#418edf'}
        />
      </div>*/}
      <div className={'input'} ref={inputEl} onClick={() => setOpen(!open)}>
        <div className={'title'}>{label.label}</div>
      </div>
      {open && (
        <Select
          top={rect(inputEl).y}
          left={rect(inputEl).x}
          queryGroup={queryGroup}
          update={update}
          toggle={() => setOpen(!open)}
        />
      )}
    </div>
  )
}

export default usePersonSelect
