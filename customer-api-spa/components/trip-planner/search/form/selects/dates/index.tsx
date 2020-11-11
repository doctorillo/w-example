import React from 'react'
import DatePicker from 'react-datepicker'
import useStyles from '../styles'
import { DateRangeSelection } from '../../../../../../types/DateRange'
import ru from 'date-fns/locale/ru'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../../theme'

export interface DRange {
  range: DateRangeSelection;

  update(range: DateRangeSelection): void;
}

function useDatesSelect(props: DRange) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { range, update } = props
  /*const [open, setOpen] = useState(false)
  const rangeRef = useRef(null)
  useClickOutside([rangeRef], () => setOpen(false))*/
  return (<div className={style.root}>
    <div className={'label'}>
      Когда
    </div>
    {/*<div className={style.icon} onClick={() => setOpen(!open)}>
      <CalendarIcon viewBox={'0 0 100 100'} width={'26px'} height={'26px'} fill={'#418edf'}/>
    </div>*/}
    <div className={'input'}>
      <DatePicker
        isClearable={true}
        locale={ru}
        dateFormat={'dd.MM.yyyy'}
        selected={range.startDate}
        selectsStart={true}
        startDate={range.startDate}
        endDate={range.endDate}
        onChange={ (date: Date) => update({...range, startDate: date})}
      />
      <DatePicker
        isClearable={true}
        locale={ru}
        dateFormat={'dd.MM.yyyy'}
        selected={range.endDate}
        selectsEnd={true}
        startDate={range.startDate}
        endDate={range.endDate}
        minDate={range.startDate}
        onChange={ (date: Date) => update({...range, endDate: date})}
      />
    </div>
  </div>)
}

export default useDatesSelect