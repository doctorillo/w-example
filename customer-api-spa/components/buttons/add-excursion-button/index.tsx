import React from 'react'
import { ExcursionCardUI } from '../../../types/bookings/ExcursionCardUI'
import Button from '@material-ui/core/Button'
import { makeLocalDateTimeString } from '../../../types/DateRange'
import { Uuid } from '../../../types/basic/Uuid'
import { PlannerExcursionItem } from '../../../types/planner/PlannerExcursionItem'
import { v4 as uuid } from 'uuid'
import { DateString } from '../../../types/basic/DateString'
import { Amount } from '../../../types/bookings/Amount'

export type AddExcursionProps = {
  card: ExcursionCardUI;
  excursionDate: DateString;
  plannerId: Uuid;
  plannerPointId: Uuid;
  clients: Uuid[];
  inCard: boolean;
  addExcursionToPlan(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerClients: Uuid[],
    item: PlannerExcursionItem,
  ): void;
}

const addButton: React.FC<AddExcursionProps> = (props: AddExcursionProps) => {
  const card = props.card
  const { clients, plannerId, plannerPointId, excursionDate, inCard, addExcursionToPlan } = props
  return (<Button
    variant="contained"
    color="secondary"
    disabled={inCard}
    onClick={() => {
      const cxs = clients.map(id => ({
        clientId: id,
        price: card.price,
        markAsDelete: false
      }))
      const total = cxs.reduce((a, x) => ({...a, value: a.value + x.price.value}), {...card.price, value: 0} as Amount)
      return addExcursionToPlan(plannerId, plannerPointId, clients, {
        id: uuid(),
        created: makeLocalDateTimeString(),
        updated: makeLocalDateTimeString(),
        date: excursionDate,
        excursionId: card.excursionId,
        accommodationPax: card.accommodationPax,
        excursionName: card.name,
        excursionOfferId: card.id,
        pickupPoint: card.pickupPoint,
        startTime: card.startTime,
        duration: card.duration,
        total: total,
        clients: cxs,
      })
    }}>
    { inCard ? 'Добавлено': 'Добавить' }
  </Button>)
}

export default addButton