import React, { Fragment } from 'react'
import { useInView } from 'react-intersection-observer'
import connect, { SearchPropertyProps } from '../../redux/modules/property-search/connect'
import connectPlanner from '../../redux/modules/trip-planner/connect'
import { PropertyCardUI } from '../../types/bookings/PropertyCardUI'
import Search from '../trip-planner/search/form'
import Card from './card'
import Filter from './filter'
import useStyles from './styles'
import More from '../buttons/more-items-button'
import ToTopButton from '../buttons/to-top-button'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../theme'

const SearchPanel = connectPlanner(Search)

const searchResultHotelCards: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
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
  const { items, allCount, priceViewMode, maxItems } = props.env
  const { setMaxItems, toProperty } = props.fn
  return (<Fragment>
    <div className={style.search} ref={ref}>
      <SearchPanel {...props.appProps} />
    </div>
    <div className={style.grid}>
      <div className={'filter'}>
        <Filter {...props} />
      </div>
      <div className={'result'}>
        {items.slice(0, maxItems).map((x: PropertyCardUI) => (<Card
          key={x.id}
          customerId={customerId}
          card={x}
          toProperty={toProperty}
          priceView={priceViewMode}/>))}
        {allCount > 0 && allCount > maxItems && (<div className={'more'}>
          <More maxItems={maxItems} filterMaxItems={setMaxItems}/>
        </div>)}
      </div>
      <ToTopButton href={'#top'} visible={!inView}/>
    </div>
  </Fragment>)
}

export default connect(searchResultHotelCards)