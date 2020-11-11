import React from 'react'
import useStyles from './styles'
import { AddToPlanFn } from '../../../../../../../redux/modules/property/connect'
import { PlannerRoom } from '../../../../../../../types/planner/PlannerRoom'
import { PlannerAccommodationItem, makeItem } from '../../../../../../../types/planner/PlannerAccommodationItem'
import Button from '@material-ui/core/Button'
import { AddRoomButtonProps } from '../AddRoomButtonProps'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../../../theme'

const addRoomToCart: React.FC<AddRoomButtonProps & AddToPlanFn> = (props: AddRoomButtonProps & AddToPlanFn) => {
  const {
    plannerId,
    plannerPointId,
    plannerPropertyId,
    propertyId,
    propertyName,
    propertyStar,
    dates,
    rooms,
    price,
    addRoomVariantToPlan,
  } = props
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)

  const inCart = rooms.reduce((acc: boolean, x: PlannerRoom) => {
    if (acc) {
      return acc
    } else {
      return !!x.variants.find(z => !z.markAsDelete && z.roomPriceUnitId === price.id && z.tariffId === price.tariffId)
    }
  }, false)
  return (
    <div className={style.root}>
      <Button
        disabled={inCart}
        className={'button'}
        variant="contained"
        color="secondary"
        onClick={() => {
          const item: PlannerAccommodationItem = makeItem(
            dates,
            propertyId,
            propertyName,
            propertyStar,
            price.id,
            price.roomTypeLabel,
            price.roomCategoryLabel,
            price.boardingLabel,
            price.tariffId,
            price.tariffLabel,
            price.groupId,
            price.nights,
            price.total,
            price.discount,
          )
          return addRoomVariantToPlan(
            plannerId,
            plannerPointId,
            plannerPropertyId,
            rooms[0].id,
            item,
          )
        }}
      >
        {inCart ? 'Вариант добавлен' : 'Добавить вариант'}
      </Button>
    </div>
  )
}

export default addRoomToCart