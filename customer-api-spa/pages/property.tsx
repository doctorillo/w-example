import React from 'react'
import PropertyPage from '../components/property'
import TopMenuItems from '../components/top-menu/items'
import Layout from '../components/layout-app'
import connect, { AppProps } from '../redux/modules/env/connect'
import { NavigationContext } from '../types/contexts/NavigationContext'


const usePropertyPage: React.FC<AppProps> = (props: AppProps) => (<Layout
  title={`Отель`}
  menuItems={<TopMenuItems ctx={NavigationContext.Hotels}  />}
  appProps={props}
>
  <PropertyPage {...props} />
</Layout>)

export default connect(usePropertyPage)
