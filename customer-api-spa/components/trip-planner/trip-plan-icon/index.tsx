import React, { useEffect } from 'react'
import connect from '../../../redux/modules/trip-planner/connect'
import IconButton from '@material-ui/core/IconButton'
import Badge from '@material-ui/core/Badge'
import TripPlanIcon from '../../icons/TripPlanIcon'
import { TripPlannerPageProps } from '../../../redux/modules/trip-planner/TripPlannerPageProps'
import { ResultKind } from '../../../types/ResultKind'

const tripPlanMenuIcon: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const { env: { status, variantCount }, fn: { fetch }} = props
  useEffect(() => {
    if (status === ResultKind.Undefined){
      fetch(props.appProps.appEnv.solverId)
    }
  }, [status])
  if (!props.env.basic){
    return null
  }
  const { preview } = props.env
  const { togglePreview } = props.fn
  return (
    <IconButton
      disabled={variantCount === 0}
      onClick={() => togglePreview(!preview)}
      color={'primary'}
    >
      <Badge badgeContent={variantCount} color="error">
        <TripPlanIcon fontSize={'large'} viewBox={'0 0 100 100'} />
      </Badge>
    </IconButton>
  )
}

export default connect(tripPlanMenuIcon)
