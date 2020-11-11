import React from 'react'
import TopMenuItems from '../../components/top-menu/items'
import Layout from '../../components/layout-app'
import PlannerSearch from '../../components/trip-planner/search'
import PlannerList from '../../components/trip-planner/list'
import { NavigationContext } from '../../types/contexts/NavigationContext'
import { AppProps } from '../../redux/modules/env/connect'
import connect from '../../redux/modules/env/connect'

const searchHotelsPage: React.FC<AppProps> = (props: AppProps) => {
  return (<Layout title="Поиск отелей" menuItems={<TopMenuItems ctx={NavigationContext.Builder}/>} appProps={props}>
    <PlannerSearch { ...props } />
    <PlannerList { ...props } />
  </Layout>)
}
export default connect(searchHotelsPage)