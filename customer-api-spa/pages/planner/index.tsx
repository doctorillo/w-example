import React, { useEffect } from 'react'

import TopMenuItems from '../../components/top-menu/items'
import Layout from '../../components/layout-app'
import Planner from '../../components/trip-planner/stepper'
import { NavigationContext } from '../../types/contexts/NavigationContext'
import connect from '../../redux/modules/trip-planner/connect'
import { NextPage } from 'next'
import { TripPlannerPageProps } from '../../redux/modules/trip-planner/TripPlannerPageProps'

const plannerPage: NextPage<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  if (!props.env){
    return null
  }
  const plannerEnv = props.env
  const preview = plannerEnv.preview
  const togglePreview = props.fn.togglePreview
  useEffect(() => {
    if (preview && togglePreview) {
      togglePreview(false)
    }
  }, [preview])
  return <Layout
      title="Бронирование"
      menuItems={<TopMenuItems ctx={NavigationContext.Planners} />}
      {...props}
    >
      <Planner {...props} />
    </Layout>
}

export default connect(plannerPage)
