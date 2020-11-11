import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import { PlannerRoom } from '../../../../types/planner/PlannerRoom'
import { PlannerAccommodationItem } from '../../../../types/planner/PlannerAccommodationItem'
import PropertyVariant from './property-variant'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    padding: theme.spacing(1),
  },
}))

export default function PropertiesForm(props: TripPlannerPageProps) {
  if (!props.env || !props.env.basic || !props.env.property) {
    return null
  }
  const {
    basic: {
      plannerId,
      plannerPoint,
      plannerPoint: { dates },
      plannerClients,
    },
    property: {
      propertyRooms,
    },
  } = props.env
  const {
    selectRoomVariant,
    removeRoomVariant,
    toProperty
  } = props.fn
  const styles = useStyles()
  return (
    <div className={styles.root}>
      {propertyRooms.map((x: PlannerRoom) => {
        const clients = plannerClients.filter(
          (z: PlannerClient) => x.clients.indexOf(z.id) > -1,
        )
        return x.variants.filter(y => !y.markAsDelete).map((y: PlannerAccommodationItem, idx: number) => (
          <PropertyVariant
            key={idx}
            idx={idx + 1}
            plannerId={plannerId}
            plannerPointId={plannerPoint.id}
            plannerRoomId={x.id}
            variantId={y.id}
            dates={dates}
            propertyName={y.propertyName}
            propertyStar={y.propertyStar}
            roomType={y.roomType}
            roomCategory={y.roomCategory}
            boarding={y.boarding}
            clients={clients}
            total={y.price}
            discount={y.discount}
            variantsCount={x.variants.length}
            selected={x.selected}
            toProperty={toProperty}
            selectRoomVariant={selectRoomVariant}
            removeRoomVariant={removeRoomVariant}
          />
        ))
      })}
    </div>
  )
}
