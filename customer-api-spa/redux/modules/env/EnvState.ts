import { DateRange } from '../../../types/DateRange'
import { LangItem } from '../../../types/LangItem'
import { ContextItem } from '../../../types/basic/ContextItem'
import { AppMessage } from '../../../types/basic/AppMessage'
import { v4 as uuid } from 'uuid'

export enum AppState {
  Undefined = 0,
  Hotels = 1,
  Excursions = 2,
  Transfers = 3,
  Orders = 4,
}

export interface EnvSelect {
  ctx: ContextItem;
  cityId: string;
  dates: DateRange;
}

export interface EnvSession {
  lang: LangItem;
  state: AppState;
  sessionId: string;
  customerId: string;
  message?: AppMessage;
}

export function initEnvState (): EnvSession {
  return {
    lang: LangItem.Ru,
    state: AppState.Undefined,
    sessionId: uuid(),
    customerId: '03e92a5f-246a-4a04-8955-93cf02f2b6ed',
    message: undefined,
  }
}
