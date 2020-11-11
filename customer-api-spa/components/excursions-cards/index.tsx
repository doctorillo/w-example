import React, { Fragment } from 'react'
import { useInView } from 'react-intersection-observer'
import connect, { CardEnv, SearchExcursionProps } from '../../redux/modules/excursion-search/connect'
import connectPlanner from '../../redux/modules/trip-planner/connect'
import Search from '../trip-planner/search/form'
import Card from './card'
import More from '../buttons/more-items-button'
import ToTopButton from '../buttons/to-top-button'
import useStyles from './styles'
import Filter from './filter'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../theme'

const SearchPanel = connectPlanner(Search)

const excursionCards: React.FC<SearchExcursionProps> = (props: SearchExcursionProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const [ref, inView] = useInView({
    threshold: 0,
    triggerOnce: false,
  })
  if (!props?.appProps?.appEnv?.customer?.id || !props.env) {
    return null
  }
  const customerId = props.appProps.appEnv.customer.id
  const { plannerId, plannerPointId, clients, items, allCount, maxItems } = props.env
  const { setMaxItems, toExcursion, selectDate, addExcursionToPlan } = props.fn
  return (<Fragment>
    <div className={style.search} ref={ref}>
      <SearchPanel {...props.appProps} />
    </div>
    <div className={style.grid}>
      <div className={'filter'}>
        <Filter {...props} />
      </div>
      <div className={'result'}>
        {items.slice(0, maxItems).map((x: CardEnv) => {
          const {card, selected, inCard} = x
          if (!selected){
            return null
          } else {
            return <Card
              key={card.id}
              customerId={customerId}
              plannerId={plannerId}
              plannerPointId={plannerPointId}
              clients={clients}
              card={card}
              excursionDate={selected}
              inCard={inCard}
              select={selectDate}
              addExcursionToPlan={addExcursionToPlan}
              toExcursion={toExcursion}/>
          }
        })}
        {allCount > 0 && allCount > maxItems && (<div className={'more'}>
          <More maxItems={maxItems} filterMaxItems={setMaxItems}/>
        </div>)}
      </div>
      <ToTopButton href={'#top'} visible={!inView}/>
    </div>
  </Fragment>)
}

export default connect(excursionCards)