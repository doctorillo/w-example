import {
  ENV_MESSAGE_RECEIVED,
  ENV_STATE_ATTACH,
  ENV_STATE_CUSTOMER_CTX_UPDATE,
  ENV_STATE_CUSTOMER_CTX_UPDATED, ENV_STATE_NAVIGATION_UPDATE,
  ENV_STATE_SET_WORKSPACE,
  ENV_STATE_SIGN_IN,
  ENV_STATE_SIGNED,
} from '../../ActionType'
import { AppMessage } from '../../../types/basic/AppMessage'
import { LangItem } from '../../../types/LangItem'
import { SignInQ } from '../../../types/contexts/SignInQ'
import { EnvState } from '../../../types/contexts/EnvState'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { CustomerContext } from '../../../types/contexts/CustomerContext'
import { NavigationContext } from '../../../types/contexts/NavigationContext'

export const envStateAttach = createAction<LangItem>(ENV_STATE_ATTACH)

export const envStateSignIn = createAction<SignInQ, typeof ENV_STATE_SIGN_IN>(ENV_STATE_SIGN_IN)
export type PayloadSignIn = PayloadAction<SignInQ, typeof ENV_STATE_SIGN_IN>

export const envStateSigned = createAction<EnvState, typeof ENV_STATE_SIGNED>(ENV_STATE_SIGNED)
export type PayloadSigned = PayloadAction<EnvState, typeof ENV_STATE_SIGNED>

export const envCustomerCtxUpdate = createAction<CustomerContext, typeof ENV_STATE_CUSTOMER_CTX_UPDATE>(ENV_STATE_CUSTOMER_CTX_UPDATE)
export type PayloadCustomerContextUpdate = PayloadAction<CustomerContext, typeof ENV_STATE_CUSTOMER_CTX_UPDATE>

export const envNavigationUpdate = createAction<NavigationContext, typeof ENV_STATE_NAVIGATION_UPDATE>(ENV_STATE_NAVIGATION_UPDATE)
export type PayloadNavigationUpdate = PayloadAction<NavigationContext, typeof ENV_STATE_NAVIGATION_UPDATE>

export const envCustomerCtxUpdated = createAction<CustomerContext, typeof ENV_STATE_CUSTOMER_CTX_UPDATED>(ENV_STATE_CUSTOMER_CTX_UPDATED)
export type PayloadCustomerContextUpdated = PayloadAction<CustomerContext, typeof ENV_STATE_CUSTOMER_CTX_UPDATED>

export const envStateSetWorkspace = createAction<string, typeof ENV_STATE_SET_WORKSPACE>(ENV_STATE_SET_WORKSPACE)
export type PayloadSetWorkspace = PayloadAction<string, typeof ENV_STATE_SET_WORKSPACE>

export const envMessageReceived = createAction<AppMessage, typeof ENV_MESSAGE_RECEIVED>(ENV_MESSAGE_RECEIVED)
export type PayloadMessageReceived = PayloadAction<AppMessage, typeof ENV_MESSAGE_RECEIVED>
