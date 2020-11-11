import React from 'react'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import { formatLocalTime, formatShortLocalDate, parseLocalDate, parseLocalTime } from '../../../../types/DateRange'
import CalendarIcon from '../../../icons/CalendarLight'
import CalculatorIcon from '../../../icons/CalculatorLight'
import BedIcon from '../../../icons/BedIcon'
import Mark from '@material-ui/icons/CheckCircle'
import Button from '@material-ui/core/Button'
import ButtonGroup from '@material-ui/core/ButtonGroup'
import useStyles from './styles'
import numeral from 'numeral'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { TripPlannerExcursionVariantFn } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'
import { PlannerExcursionItem, PlannerExcursionItemId } from '../../../../types/planner/PlannerExcursionItem'
import { DateString } from '../../../../types/basic/DateString'

export interface ExcursionVariantDateProps {
  idx: number;
  plannerId: string;
  plannerPointId: string;
  date: DateString;
  clients: PlannerClient[];
  variants: PlannerExcursionItem[];
  selected: PlannerExcursionItemId[];
  variantsCount: number;
}

export default function ExcursionVariant(
  props: ExcursionVariantDateProps & TripPlannerExcursionVariantFn
) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const {
    plannerId,
    plannerPointId,
    date,
    clients,
    variants,
    selected,
    selectExcursionVariant,
    unselectExcursionVariant,
    removeExcursionVariant,
  } = props
  const d = formatShortLocalDate(parseLocalDate(date))
  return (
    <div className={style.root}>
      <div className={'header'}>
        {d}
      </div>
      {variants.map((x, seqNr) => (<div key={x.id} className={'container'}>
        <div className={'count'}>
          <div
            className={clsx({
              counter: true,
              active: selected.includes(x.id),
            })}
          >
            {selected.includes(x.id) ? <Mark /> : seqNr +  1}
          </div>
        </div>
        <div className={'data'}>
          <div className={'title'}>
            {x.excursionName}
          </div>
          <div className={'description'}>
            <div className={'item'}>
              <div className={'icon'}>
                <CalendarIcon viewBox="0 0 100 100" />
              </div>
              <div className={'content'}>
                <div className={'label'}>Время начала:</div>
                <div className={'value'}>
                  {formatLocalTime(parseLocalTime(x.startTime))}
                </div>
              </div>
            </div>
            {x.accommodationPax > 0 &&<div className={'item'}>
              <div className={'icon'}>
                <BedIcon viewBox="0 0 100 100" />
              </div>
              <div className={'content'}>
                <div className={'label'}>Тип проживания:</div>
                <div className={'value'} >
                  {x.accommodationPax === 1 && 'в одно-местном номере'}
                  {x.accommodationPax === 2 && 'в двух-местном номере'}
                  {x.accommodationPax === 3 && 'в трех-местном номере'}
                </div>
              </div>
            </div>}
          </div>
          <div className={'price'}>
            <div className={'item'}>
              <div className={'icon'}>
                <CalculatorIcon viewBox="0 0 100 100" />
              </div>
              <div className={'content'}>
                <div className={'label'}>Кол-во туристов</div>
                <div className={'value'}>{clients.length}</div>
                <div className={'label'}>Цена с человека</div>
                <div className={'value'}>{numeral(x.clients[0].price.value).format(',')} €</div>
              </div>
            </div>
          </div>
        </div>
        <div className={'action'}>
          <div
            className={clsx({
              total: true,
              active: selected.includes(x.id),
            })}
          >
            {numeral(x.total.value).format(',')} €
          </div>
          {selected.includes(x.id) && (
            <div className={'button'}>
              <ButtonGroup size="small" variant="outlined" color="secondary">
                <Button
                  //color={'error'}
                  onClick={() =>
                    removeExcursionVariant(
                      plannerId,
                      plannerPointId,
                      clients.map(x => x.id),
                      x.id
                    )
                  }
                >
                  Удалить
                </Button>
                <Button
                  color="secondary"
                  onClick={() =>
                    unselectExcursionVariant(
                      plannerId,
                      plannerPointId,
                      x.id
                    )
                  }
                >
                  Снять выбор
                </Button>
              </ButtonGroup>
            </div>
          )}
        {!selected.includes(x.id) && (
            <div className={'button'}>
              <ButtonGroup size="small" variant="outlined" color="secondary">
                <Button
                  //color={'error'}
                  onClick={() =>
                    removeExcursionVariant(
                      plannerId,
                      plannerPointId,
                      clients.map(x => x.id),
                      x.id
                    )
                  }
                >
                  Удалить
                </Button>
                <Button
                  color="secondary"
                  onClick={() =>
                    selectExcursionVariant(
                      plannerId,
                      plannerPointId,
                      x.id
                    )
                  }
                >
                  Выбрать
                </Button>
              </ButtonGroup>
            </div>
          )}
        </div>
      </div>))}
    </div>
  )
}
