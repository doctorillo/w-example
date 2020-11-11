import { composeWithDevTools } from 'redux-devtools-extension'
import { createStore, applyMiddleware } from 'redux'
import { createEpicMiddleware } from 'redux-observable'
import { BehaviorSubject } from 'rxjs'
import { switchMap } from 'rxjs/operators'
import reducer from './reducers'
import rootEpic from './epics'
import nextReduxWrapper from 'next-redux-wrapper'

const production: boolean = process.env.NODE_ENV === 'production'

export type RootState = ReturnType<typeof reducer>;
export type StateWrapper = {
  value: RootState;
}

const epic$ = new BehaviorSubject(rootEpic)
const hotReloadingEpic = (...args: any[]) =>
  epic$.pipe(switchMap(epic => epic(...args)))

export function configureStore(initialState: {}) {
  console.log('configure store')
  const epicMiddleware = createEpicMiddleware()
  const middleware = applyMiddleware(epicMiddleware)
  const reduxMiddleware = production ? middleware : composeWithDevTools(middleware)
  const store = createStore(reducer, initialState, reduxMiddleware)
  epicMiddleware.run(hotReloadingEpic as any)
  if (!production && module.hot) {
    console.log('reducer hot reload')
    module.hot.accept('./reducers', () => {
      const nextReducer = require('./reducers').default
      store.replaceReducer(nextReducer)
    })
    module.hot.accept('./epics', () => {
      const nextEpic = require('./epics').default
      epic$.next(nextEpic)
    })
  }
  return store
}

export default function(component: any) {
  return nextReduxWrapper(configureStore)(component)
}