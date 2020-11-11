import React, { Fragment, useRef } from 'react'
import Link from 'next/link'
import { useClickOutside } from 'use-events'
import connect from '../../../redux/modules/trip-planner/connect'
import ActivePointView, { mapToView } from './active-point'
import useStyles from './styles'
import { PlannerPoint } from '../../../types/planner/PlannerPoint'
import { makeLabel } from '../../../types/planner/PlannerProperty'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'
import { TripPlannerPageProps } from '../../../redux/modules/trip-planner/TripPlannerPageProps'

const tripPlannerView: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const nodeRef = useRef(null)
  const {
    fn: {
      togglePreview,
    },
  } = props
  useClickOutside([nodeRef], () => togglePreview(false))
  const mapView = mapToView(props)
  if (!mapView || !props.env || !props.env.basic || !props.appProps?.appEnv?.workspace?.businessPartyId) {
    return null
  }
  const {
    env: {
      lang,
      basic: {
        plannerPoint,
        plannerPoints,
        plannerClients,
      },
      preview,
      variantCount
    },
  } = props
  const points: PlannerPoint[] = (!plannerPoint
      ? []
      : [plannerPoint, ...plannerPoints]
  ).filter((x: PlannerPoint) => x.dates && x.property && x.point)
  const label = plannerPoint && plannerPoint.property && makeLabel(plannerClients, plannerPoint.property, lang)
  return variantCount === 0 || !preview ? (
    <Fragment/>
  ) : (
    <div ref={nodeRef} className={style.root}>
      <div className={'action'}>
        {label && label.label}
      </div>
      {points.map((x: PlannerPoint) => {
            return x.property && x.dates && x.point && <ActivePointView
              key={x.id}
              {...mapView}
            />
          },
        )}
      <Link href="/planner">
        <button className={'button'}>
          Перейти к бронированию
        </button>
      </Link>

    </div>
  )
}

export default connect(tripPlannerView)
