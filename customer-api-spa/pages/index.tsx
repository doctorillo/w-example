import React from 'react'
import { NextPage } from 'next'
import connect, { AppProps } from '../redux/modules/env/connect'
import TopMenuItems from '../components/top-menu/items'
import Layout from '../components/layout-app'

const Index: NextPage<AppProps> = (props: AppProps & { children?: React.ReactNode }) => {
  const { appEnv: { navigation } } = props
  const menuItems = <TopMenuItems ctx={navigation} />
  return (<Layout title="V-alfa" menuItems={menuItems} appProps={props} />)
}

export default connect(Index)
