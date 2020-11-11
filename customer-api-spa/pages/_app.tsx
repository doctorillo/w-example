import React from 'react'

//import NextRouter from 'next/router'
import { Provider } from 'react-redux'
import App, { AppInitialProps } from 'next/app'
import withRedux, { RootState } from '../redux'
import { ThemeProvider } from '@material-ui/styles'
import theme from '../components/theme'
import '../public/css/empty.css'
import { ResultKind } from '../types/ResultKind'
import { ENV_STATE_ATTACH } from '../redux/ActionType'
import { LangItem } from '../types/LangItem'
import { baseUrl } from '../redux/axios'
import { STATE_ENV } from '../redux/modules/env/reducers'
import { AppPropsType } from 'next/dist/next-server/lib/utils'
import { Router } from 'next/dist/client/router'
import { EnhancedStore } from '@reduxjs/toolkit'
import { AppProps } from '../redux/modules/env/connect'
import '../public/css/date-picker.css'

/*if (process.env.NODE_ENV !== 'production') {
  NextRouter.events.on('routeChangeComplete', () => {
    const path = '/_next/static/css/styles.chunk.css'
    const chunksNodes: NodeListOf<Element & { href: string }> = document.querySelectorAll(`link[href*="${path}"]:not([rel=preload])`)
    if (chunksNodes.length) {
      const timestamp = new Date().valueOf()
      chunksNodes[0].href = `${path}?ts=${timestamp}`
    }
  })
}*/

export type MyAppProps = AppInitialProps & AppPropsType<Router, AppProps> & { store: EnhancedStore<RootState> }

class MyApp extends App<MyAppProps> {
  componentDidMount(): void {
    const { store } = this.props
    const state: STATE_ENV = store.getState().sliceEnv
    if (state.status === ResultKind.Undefined) {
      store.dispatch({
        type: ENV_STATE_ATTACH,
        payload: LangItem.Ru,
      })
      const hrf = `${baseUrl}/login`
      if (hrf !== window.location.href) {
        window.location.href = hrf
      }
    }
    // Remove the server-side injected CSS.
    const jssStyles = document && document.querySelector('#jss-server-side')
    if (jssStyles) {
      jssStyles?.parentNode?.removeChild(jssStyles)
    }
  }


  render(): any {
    const { Component, pageProps, store } = this.props
    return (
      <Provider store={store}>
        <ThemeProvider theme={theme}>
          <Component {...pageProps}/>
        </ThemeProvider>
      </Provider>
    )
  }
}

export default withRedux(MyApp)
