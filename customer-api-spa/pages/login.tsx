import React from 'react'
import connect, { AppProps } from '../redux/modules/env/connect'
import Login from '../components/login'

function LoginPage (props: AppProps){

  return (<Login {...props} />)
}

export default connect(LoginPage)