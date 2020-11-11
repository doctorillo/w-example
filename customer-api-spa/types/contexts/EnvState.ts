import { ResultKind } from '../ResultKind'
import { NavigationContext } from './NavigationContext'
import { AppMessage } from '../basic/AppMessage'
import { LangItem } from '../LangItem'
import { WorkspaceUI } from './WorkspaceUI'
import { ContextEnvUI } from './ContextEnvUI'
import { Nullable } from '../Nullable'
import { CustomerContext } from './CustomerContext'
import { PointOption } from '../geo/PointOption'
import { DateRange } from '../DateRange'

export type EnvState = {
  solverId: string;
  solverName: string;
  email: string;
  lang: LangItem;
  navigation: NavigationContext;
  workspace: Nullable<WorkspaceUI>;
  workspaces: WorkspaceUI[];
  customer: Nullable<CustomerContext>;
  messages: AppMessage[];
  status: ResultKind;
}

export const initEnvState = (): EnvState => ({
  solverId: '',
  solverName: '',
  email: '',
  lang: LangItem.Ru,
  navigation: NavigationContext.Builder,
  workspace: null,
  workspaces: [],
  customer: null,
  messages: [],
  status: ResultKind.Undefined,
})

export const of = (ctx: ContextEnvUI, navigation: NavigationContext): EnvState => ({
  solverId: ctx.solverId,
  solverName: ctx.solverName,
  email: ctx.email,
  lang: ctx.preferredLang,
  navigation,
  workspace: ctx.workspace,
  workspaces: ctx.workspaces,
  customer: null,
  messages: [],
  status: ResultKind.Completed,
})

export interface EnvAsk {
  lang: LangItem;
  solverId: string;
  solverName: string;
  customerId: string;
  customerName: string;
  point: Nullable<PointOption>;
  dates: Nullable<DateRange>;
  navigation: NavigationContext;
  status: ResultKind;
}

export const envAsk = (env: EnvState): Nullable<EnvAsk> => {
  if (!env.workspace || !env.customer){
    return null
  }
  return {
    lang: env.lang,
    solverId: env.solverId,
    solverName: env.solverName,
    customerId: env.customer.id,
    customerName: env.customer.name,
    point: env.customer.point,
    dates: env.customer.dates,
    navigation: env.navigation,
    status: env.status
  }
}