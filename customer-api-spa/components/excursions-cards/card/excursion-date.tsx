import React from 'react'
import { Uuid } from '../../../types/basic/Uuid'
import { NativeSelect } from '@material-ui/core'
import format from 'date-fns/format'
import ru from 'date-fns/locale/ru/index'
import { parseLocalDate } from '../../../types/DateRange'
import { DateString } from '../../../types/basic/DateString'

export interface ExcursionDateSelect {
  excursionOfferId: Uuid;
  selected: DateString;
  dates: string[];

  select(excursionOfferId: Uuid, date: string): void;
}

export const excursionDate: React.FC<ExcursionDateSelect> = (props: ExcursionDateSelect) => {
  const { excursionOfferId, selected, dates, select } = props
  return <NativeSelect
    disableUnderline={true}
    disabled={dates.length === 1}
    value={selected}
    inputProps={{
      name: 'excursion-date',
      id: 'excursion-date-native',
    }}
    onChange={({ target: { value } }) => {
      select(excursionOfferId, value)
    }}
  >
    {dates.map((x, idx) => (
      <option key={idx} value={x}>
        {format(parseLocalDate(x), 'dd MMMM, cccc', { locale: ru })}
      </option>))}
  </NativeSelect>
}

export default excursionDate