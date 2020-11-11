import React, { Fragment } from 'react'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import { Amount } from '../../../../types/bookings/Amount'
import { DateRange } from '../../../../types/DateRange'
import CalendarIcon from '../../../icons/CalendarLight'
import CalculatorIcon from '../../../icons/CalculatorLight'
import BedIcon from '../../../icons/BedIcon'
import Mark from '@material-ui/icons/CheckCircle'
import Button from '@material-ui/core/Button'
import ButtonGroup from '@material-ui/core/ButtonGroup'
import parse from 'date-fns/parseISO'
import format from 'date-fns/format'
import ru from 'date-fns/locale/ru'
import differenceInDays from 'date-fns/differenceInDays'
import useStyles from './styles'
import numeral from 'numeral'
import { Nullable } from '../../../../types/Nullable'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { TripPlannerPropertyVariantFn } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'

export interface PropertyVariantProps {
  idx: number;
  plannerId: string;
  plannerPointId: string;
  plannerRoomId: string;
  dates: DateRange;
  variantId: string;
  propertyName: string;
  propertyStar: number;
  roomType: string;
  roomCategory: string;
  boarding: string;
  clients: PlannerClient[];
  total: Amount;
  discount: Nullable<Amount>;
  variantsCount: number;
  selected: Nullable<string>;
}

export default function PropertyVariant(
  props: PropertyVariantProps & TripPlannerPropertyVariantFn
) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const {
    idx,
    plannerId,
    plannerPointId,
    plannerRoomId,
    variantId,
    propertyName,
    propertyStar,
    dates,
    clients,
    roomType,
    roomCategory,
    boarding,
    total,
    discount,
    selected,
    selectRoomVariant,
    removeRoomVariant,
  } = props
  const dateFrom = parse(dates.from)
  const dateTo = parse(dates.to)
  const nights = differenceInDays(dateTo, dateFrom)
  const discountValue = !discount ? 0.0 : discount.value
  const perNight: number = Math.round(
    ((total.value + discountValue) * 10) / (nights * clients.length)
  ) / 10
  return (
    <div className={style.root}>
      <div className={'count'}>
        <div
          className={clsx({
            counter: true,
            active: variantId === selected,
          })}
        >
          {variantId === selected ? <Mark /> :idx}
        </div>
      </div>
      <div className={'data'}>
        <div className={'title'}>
          {propertyName} {propertyStar}*
        </div>
        <div className={'description'}>
          <div className={'item'}>
            <div className={'icon'}>
              <CalendarIcon viewBox="0 0 100 100" />
            </div>
            <div className={'content'}>
              <div className={'label'}>Дата и время заезда:</div>
              <div className={'value'}>
                {format(dateFrom, 'd MMM, EEEEEE', { locale: ru })} 14:00
              </div>
              <div className={'label'}>Дата и время выезда:</div>
              <div className={'value'}>
                {format(dateTo, 'd MMM, EEEEEE', { locale: ru })} 10:00
              </div>
            </div>
          </div>
          <div className={'item'}>
            <div className={'icon'}>
              <BedIcon viewBox="0 0 100 100" />
            </div>
            <div className={'content'}>
              <div className={'label'}>Тип номера:</div>
              <div className={'value'}>{roomType}</div>
              <div className={'label'}>Категория номера:</div>
              <div className={'value'}>{roomCategory}</div>
              <div className={'label'}>Питание:</div>
              <div className={'value'}>{boarding}</div>
            </div>
          </div>
        </div>
        <div className={'price'}>
          <div className={'item'}>
            <div className={'icon'}>
              <CalculatorIcon viewBox="0 0 100 100" />
            </div>
            <div className={'content'}>
              <div className={'label'}>Кол-во гостей</div>
              <div className={'value'}>{clients.length}</div>
              <div className={'label'}>Цена за ночь с человека</div>
              <div className={'value'}>{numeral(perNight).format('0.0')} €</div>
              <div className={'label'}>Кол-во ночей</div>
              <div className={'value'}>{nights}</div>
              {discountValue > 0 && (
                <Fragment>
                  <div className={'label'}>Скидка</div>
                  <div className={'value'}>
                    - {numeral(discountValue).format(',')} €
                  </div>
                </Fragment>
              )}
              <div className={'label'}>Итого за номер</div>
              <div className={'value'}>
                {numeral(total.value).format(',')} €
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className={'action'}>
        <div
          className={clsx({
            total: true,
            active: selected === variantId,
          })}
        >
          {numeral(total.value).format(',')} €
        </div>
        {selected !== variantId && (
          <div className={'button'}>
            <ButtonGroup size="small" variant="outlined" color="primary">
              <Button
                //color={'error'}
                onClick={() =>
                  removeRoomVariant(
                    plannerId,
                    plannerPointId,
                    plannerRoomId,
                    variantId
                  )
                }
              >
                Удалить
              </Button>
              <Button
                color="primary"
                onClick={() =>
                  selectRoomVariant(
                    plannerId,
                    plannerPointId,
                    plannerRoomId,
                    variantId
                  )
                }
              >
                Выбрать
              </Button>
            </ButtonGroup>
          </div>
        )}
      </div>
    </div>
  )
}
