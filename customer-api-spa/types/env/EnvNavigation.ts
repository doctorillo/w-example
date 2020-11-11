import { NavigationContext } from '../contexts/NavigationContext'
import { ResultKind } from '../ResultKind'

export interface EnvNavigation {
  navigation: NavigationContext;
  auth: ResultKind;
}

export const makeEnvNavigation = (): EnvNavigation => ({
  navigation: NavigationContext.Builder,
  auth: ResultKind.Undefined
})