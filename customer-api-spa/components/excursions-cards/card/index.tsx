import React from 'react'
import useStyles from './styles'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'
import { ExcursionCardUI } from '../../../types/bookings/ExcursionCardUI'
import format from 'date-fns/format'
import ru from 'date-fns/locale/ru/index'
import TagItem from './tag'
import DateSelect from './excursion-date'
import { parseLocalTime } from '../../../types/DateRange'
import { Uuid } from '../../../types/basic/Uuid'
import { DateString } from '../../../types/basic/DateString'
import { PlannerExcursionItem } from '../../../types/planner/PlannerExcursionItem'
import AddButton from '../../buttons/add-excursion-button'

export type ExcursionCardProps = {
  card: ExcursionCardUI;
  excursionDate: DateString;
  customerId: Uuid;
  plannerId: Uuid;
  plannerPointId: Uuid;
  clients: Uuid[];
  inCard: boolean;
  select(excursionOfferId: Uuid, date: DateString): void;
  addExcursionToPlan(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerClients: Uuid[],
    item: PlannerExcursionItem,
  ): void;
  toExcursion(excursionId: Uuid, supplierId: Uuid, customerId: Uuid): void;
}

const excursionCard: React.FC<ExcursionCardProps> = (props: ExcursionCardProps) => {
  const theme = useTheme<AppTheme>()
  const styles = useStyles(theme)
  const { card } = props
  const { inCard, clients, plannerId, plannerPointId, card: { name }, excursionDate, select, addExcursionToPlan } = props
  return (<div className={styles.root}>
    <div className={styles.photo}>
      <img src="/no-img.png" alt="card"/>
    </div>
    <div className={styles.description}>
      <div className={'title'}>
        {name}
        {/*<Link href={'/excursion'}>
          {name}
        </Link>*/}
      </div>
      <div className={'container'}>
        <div className={'main'}>
          <div className={'tags'}>
            {card.tags.map(x => (<TagItem key={x} tag={x}/>))}
          </div>
          <div className={'accommodation'}>
            {card.accommodationPax === 0 ? '' : `Размещение в ${card.accommodationPax}-местном номере`}
          </div>
          <div className={'start'}>
            начало: {card.startTime && format(parseLocalTime(card.startTime), 'HH:mm', { locale: ru })}
          </div>
          <div className={'date'}>
            <DateSelect
              excursionOfferId={card.id}
              dates={card.dates}
              selected={excursionDate} select={select}/>
          </div>
        </div>
        <div className={'price'}>
          <div className={'amount'}>
            {card.price.value} €
          </div>
          <div className={'booking'}>
            <AddButton
              inCard={inCard}
              plannerId={plannerId}
              plannerPointId={plannerPointId}
              clients={clients}
              card={card}
              excursionDate={excursionDate}
              addExcursionToPlan={addExcursionToPlan}/>
          </div>
        </div>
      </div>
    </div>
  </div>)
}

export default excursionCard