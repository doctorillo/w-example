import React, { useEffect, useState } from 'react'
import Link from 'next/link'
import { useInView } from 'react-intersection-observer'
import SubMenu from './sub-menu'
import ToTopButton from '../buttons/to-top-button'
import Header from './header'
import Article from './page-article'
import Map from './map'
import Rooms from './rooms'
import Markdown from 'react-markdown'
import useStyles from './styles'
import Search from '../trip-planner/search/form'
import connect, { PropertyPageProps } from '../../redux/modules/property/connect'
import connectPlanner from '../../redux/modules/trip-planner/connect'
import { ResultKind } from '../../types/ResultKind'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../theme'

const SearchPanel = connectPlanner(Search)

const propertyComponent: React.FC<PropertyPageProps> = (props: PropertyPageProps) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const { propertyProps, status, propertyFn: { fetchPropertyData } } = props
  useEffect(() => {
    if (status === ResultKind.Undefined) {
      fetchPropertyData()
    }
  }, [status])
  const [openMap, setOpenMap] = useState(false)
  const [topRef, inView] = useInView({
    threshold: 0,
    triggerOnce: false,
  })
  if (!propertyProps){
    return null
  }
  const { point, card, description, infrastructure } = propertyProps
  const hasDescription = description && description.description && description.description.length > 1
  const hasGuestTerm = description && description.guestTerm && description.guestTerm.length > 1
  const hasTaxTerm = description && description.taxTerm && description.taxTerm.length > 1
  const hasCancellationTerm = description && description.cancellationTerm && description.cancellationTerm.length > 1
  const hasInfo = hasGuestTerm || hasTaxTerm || hasCancellationTerm
  return !card ? <React.Fragment/> : (
    <div id={'top'} className={style.root}>
      <div className={'panel'}>
        <SearchPanel {...props.appProps} />
      </div>
      <Header {...props} />
      <SubMenu>
        <nav ref={topRef} className={style.menu}>
          {point && <a href={`#maps`} onClick={() => setOpenMap(!openMap)}>{ !openMap ? 'Показать на карте' : 'Закрыть карту' }</a>}
          <a href={`#rooms`}>Номера</a>
          {hasDescription && <a href={`#description`}>Описание</a>}
          {hasInfo && <a href={`#info`}>Дополнительная иформация</a>}
          <Link href={'/hotels'}>
            Вернуться на список отелей
            {/*<a href={'#back'} onClick={toList}>Вернуться на список отелей</a>*/}
          </Link>

        </nav>
      </SubMenu>
      {openMap && point && <Map {...props} />}
      <Rooms {...props} />
      {hasDescription &&
      <Article anchor={'description'} header="Описание" subHeader={null}>
        <Markdown source={description?.description || ''} />
      </Article>}
      {infrastructure.length > 0 && <Article anchor={'infra'} header="В отеле" subHeader={null}>
        <div className={style.list}>
          {infrastructure.map((x, idx: number) => <Markdown key={idx} source={x}/>)}
        </div>
      </Article>}
      {hasInfo &&
      <Article anchor={'info'} header="Дополнительная иформация" subHeader={null}>
        {hasGuestTerm && <Markdown source={description?.guestTerm || ''}/>}
        {hasTaxTerm && <Markdown source={description?.taxTerm || ''}/>}
        {hasCancellationTerm && <Markdown source={description?.cancellationTerm || ''}/>}
      </Article>}
      <ToTopButton href={'#top'} visible={!inView} />
    </div>
  )
}

export default connect(propertyComponent)