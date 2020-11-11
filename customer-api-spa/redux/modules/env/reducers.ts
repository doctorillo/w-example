import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { EnvState, initEnvState } from '../../../types/contexts/EnvState'
import { WorkspaceUI } from '../../../types/contexts/WorkspaceUI'
import {
  envCustomerCtxUpdated,
  envMessageReceived,
  envNavigationUpdate,
  envStateSetWorkspace,
  envStateSigned,
  PayloadCustomerContextUpdated,
  PayloadMessageReceived,
  PayloadNavigationUpdate,
  PayloadSetWorkspace,
  PayloadSigned,
} from './actions'

export type STATE_ENV = EnvState

const signedR: CaseReducer<EnvState, PayloadSigned> = (_: EnvState, action) => action.payload

const navigationUpdatedR: CaseReducer<EnvState, PayloadNavigationUpdate> = (state, action) => ({
  ...state,
  navigation: action.payload,
})

const contextUpdatedR: CaseReducer<EnvState, PayloadCustomerContextUpdated> = (state, action) => ({
  ...state,
  customer: action.payload,
})

const stateSetWorkspaceR: CaseReducer<EnvState, PayloadSetWorkspace> = (state, action) => {
  const w = state.workspaces.find((x: WorkspaceUI) => x.businessPartyId === action.payload)
  return !w ? state : { ...state, workspace: w }
}

const messageReceivedR: CaseReducer<EnvState, PayloadMessageReceived> = (state , action) => ({
  ...state,
  messages: [...state.messages, action.payload],
})

export const reducerEnv = createReducer<STATE_ENV>(initEnvState(), builder => builder
  .addCase(envStateSigned, signedR)
  .addCase(envNavigationUpdate, navigationUpdatedR)
  .addCase(envCustomerCtxUpdated, contextUpdatedR)
  .addCase(envStateSetWorkspace, stateSetWorkspaceR)
  .addCase(envMessageReceived, messageReceivedR)
)