import React from 'react'
import connect, { AppProps } from '../../redux/modules/env/connect'
import Layout from '../../components/layout-app'
import Result from '../../components/hotel-cards'
import TopMenuItems from '../../components/top-menu/items'
import { NavigationContext } from '../../types/contexts/NavigationContext'

function useHotelCardsPage (props: AppProps) {
  /*const {
    amenityStatus,
    fetchAmenities,
    facilityStatus,
    fetchFacilities,
    therapyStatus,
    fetchTherapies,
    filterParams
  } = props
  useEffect(() => {
    if (amenityStatus === ResultKind.Builder) {
      fetchAmenities()
    }
    if (facilityStatus === ResultKind.Builder) {
      fetchFacilities()
    }
    if (therapyStatus === ResultKind.Builder) {
      fetchTherapies()
    }
  }, [amenityStatus])*/
  return (
    <Layout title="Результат поиска отелей"
            menuItems={<TopMenuItems ctx={NavigationContext.Hotels}/>} appProps={props}>
      <Result {...props} />
    </Layout>
  )
}

export default connect(useHotelCardsPage)
