import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import ExcursionVariant from './excursion-variant'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'
import { PlannerExcursionVariantDate } from '../../../../types/planner/PlannerExcursionVariantDate'

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    padding: theme.spacing(1),
  },
}))

export default function ExcursionsForm(props: TripPlannerPageProps) {
  if (!props.env || !props.env.basic || !props.env.excursion) {
    return null
  }
  const {
    basic: {
      plannerId,
      plannerPoint,
      plannerClients,
    },
    excursion: {
      excursionItems,
      excursionSelected
    },
  } = props.env
  const {
    addExcursionToPlan,
    selectExcursionVariant,
    unselectExcursionVariant,
    removeExcursionVariant,
  } = props.fn
  const styles = useStyles()
  return (
    <div className={styles.root}>
      {excursionItems.map((x: PlannerExcursionVariantDate, idx: number) => (<ExcursionVariant
        key={idx}
        idx={idx + 1}
        plannerId={plannerId}
        plannerPointId={plannerPoint.id}
        date={x.date}
        clients={plannerClients}
        variants={x.variants}
        selected={excursionSelected}
        variantsCount={x.variants.length}
        addExcursionToPlan={addExcursionToPlan}
        selectExcursionVariant={selectExcursionVariant}
        unselectExcursionVariant={unselectExcursionVariant}
        removeExcursionVariant={removeExcursionVariant}
      />))}
    </div>
  )
}
