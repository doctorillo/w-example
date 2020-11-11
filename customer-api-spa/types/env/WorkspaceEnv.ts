import { ResultKind } from '../ResultKind'
import { AppMessage } from '../basic/AppMessage'
import { ContextEnvUI } from '../contexts/ContextEnvUI'
import { LangItem } from '../LangItem'
import { v4 as uuid } from 'uuid'
import { AppState } from './AppState'
import { Nullable } from '../Nullable'

export interface WorkspaceEnv {
  lang: LangItem;
  sessionId: string;
  fingerprint: string;
  customerId: string;
  appCtx: AppState;
  userCtx: Nullable<ContextEnvUI>;
  message: Nullable<AppMessage>;
  status: ResultKind;
  created: Date;
}

export function initWorkspaceEnv (): WorkspaceEnv {
  return {
    lang: LangItem.Ru,
    sessionId: uuid(),
    fingerprint: '',
    customerId: '03e92a5f-246a-4a04-8955-93cf02f2b6ed',
    appCtx: AppState.Undefined,
    userCtx: null,
    message: null,
    status: ResultKind.Undefined,
    created: new Date(),
  }
}