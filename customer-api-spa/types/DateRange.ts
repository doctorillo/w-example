import addDays from 'date-fns/addDays'
import format from 'date-fns/format'
import parse from 'date-fns/parse'
import ru from 'date-fns/locale/ru/index'

export type DateRange = {
  from: string;
  to: string;
}

export type LocalDateRange = {
  start: Date;
  end: Date;
}

export type DateRangeSelection = {
  key: 'selection';
  startDate: Date;
  endDate: Date;
}

export const formatLocalDate = (date: Date): string => format(date, 'yyyy-MM-dd')

export const parseLocalDate = (date: string): Date => parse(date, 'yyyy-MM-dd', new Date())

export const formatShortLocalDate = (date: Date, locale: Locale = ru): string => format(date, 'dd MMMM, cccc', { locale })

export const makeDateRange = (from: string, to: string): LocalDateRange => ({
  start: parseLocalDate(from),
  end: parseLocalDate(to)
})

export const formatLocalDateTime = (date: Date): string => format(date, "yyyy-MM-dd'T'HH:mm:ss")

export const parseLocalDateTime = (date: string): Date => parse(date, "yyyy-MM-dd'T'HH:mm:ss", new Date())

export const parseLocalTime = (date: string): Date => parse(date, "HH:mm:SS", new Date())

export const formatLocalTime = (date: Date): string => format(date, "HH:mm")

export const makeLocalDateTimeString = () => formatLocalDateTime(new Date())

export function initDateRange (from = 14, to = 21): DateRange {
  return ({
    from: format(addDays(new Date(), from), 'yyyy-MM-dd'),
    to: format(addDays(new Date(), to), 'yyyy-MM-dd'),
  })
}

