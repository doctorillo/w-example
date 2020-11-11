import { connect } from 'react-redux'
import { LangItem } from '../../../types/LangItem'
import {
  envCustomerCtxUpdate,
  envNavigationUpdate,
  envStateAttach,
  envStateSetWorkspace,
  envStateSignIn,
} from './actions'
import { SignInQ } from '../../../types/contexts/SignInQ'
import { EnvState } from '../../../types/contexts/EnvState'
import { RootState } from '../../index'
import { Dispatch } from 'redux'
import { CustomerContext } from '../../../types/contexts/CustomerContext'
import { NavigationContext } from '../../../types/contexts/NavigationContext'

export interface AppFn {

  attachSession(lang: LangItem): void;

  signIn(payload: SignInQ): void;

  navigationUpdate(payload: NavigationContext): void;

  customerContextUpdate(payload: CustomerContext): void;

  workspaceSelect(partyId: string): void;
}

const mapStateToProps = (state: RootState): EnvState => state.sliceEnv

const mapDispatchToProps = (dispatch: Dispatch): AppFn => {
  return {
    attachSession(lang: LangItem): void {
      dispatch(envStateAttach(lang))
    },
    signIn(payload: SignInQ): void {
      dispatch(envStateSignIn(payload))
    },
    navigationUpdate(payload: NavigationContext): void {
      dispatch(envNavigationUpdate(payload))
    },
    customerContextUpdate(payload: CustomerContext): void {
      dispatch(envCustomerCtxUpdate(payload))
    },
    workspaceSelect(businessPartyId: string): void {
      dispatch(envStateSetWorkspace(businessPartyId))
    },
  }
}
export type AppProps = {
  appEnv: EnvState;
  appFn: AppFn;
}

const mergeProps = (
  stateProps: EnvState,
  dispatchProps: AppFn,
): AppProps => ({ appEnv: stateProps, appFn: dispatchProps})

const make = (comp: any) =>
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mergeProps,
  )(comp)

export default make
