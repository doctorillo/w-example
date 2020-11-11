import React, { Fragment } from 'react'
import { PointPlanViewProps } from '../active-point'
import Variant from './variant-item'
import { PlannerExcursionVariantDate } from '../../../../types/planner/PlannerExcursionVariantDate'

const plannerExcursionView: React.FC<PointPlanViewProps> = (props: PointPlanViewProps) => {
  const {
    plannerId,
    plannerPointId,
    plannerClients,
    plannerExcursion,
    removeExcursionVariant,
  } = props
  if (!plannerExcursion || plannerExcursion.excursionVariantCount === 0){
    return null
  }
  return (
    <Fragment>
      {plannerExcursion.excursionItems.map((x: PlannerExcursionVariantDate, idx) => (
        <Variant
          key={idx}
          idx={idx}
          plannerId={plannerId}
          plannerPointId={plannerPointId}
          plannerClients={plannerClients}
          item={x}
          removeExcursionVariant={removeExcursionVariant}/>),
      )}
    </Fragment>
  )
}

export default plannerExcursionView