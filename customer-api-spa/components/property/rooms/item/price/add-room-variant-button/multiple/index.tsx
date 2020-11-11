import React from 'react'
import Button from '@material-ui/core/Button'
import useStyles from './styles'
import { AddToPlanFn, GroupPrice } from '../../../../../../../redux/modules/property/connect'
import { PlannerRoom } from '../../../../../../../types/planner/PlannerRoom'
import { PlannerAccommodationItem } from '../../../../../../../types/planner/PlannerAccommodationItem'
import { makeItem } from '../../../../../../../types/planner/PlannerAccommodationItem'
import { AddRoomButtonProps } from '../AddRoomButtonProps'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../../../theme'

function AddRoomToCart(props: AddRoomButtonProps & AddToPlanFn) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
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
    pricesInCart,
    addRoomVariantToPlan,
  } = props
  const variants = rooms.filter(
    (x: PlannerRoom) =>
      !pricesInCart.find(
        (y: GroupPrice) => x.id === y.roomId && y.priceUnitId === price.id
      ) && price.roomPosition.includes(x.position)
  )
  const inCart = variants.length === 0
  return (
    <div className={style.root}>
      {inCart && <div className={'disabled'}>Номер в корзине</div>}
      {!inCart && (
        <Button
          variant="outlined"
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
              price.discount
            )
            return addRoomVariantToPlan(
              plannerId,
              plannerPointId,
              plannerPropertyId,
              rooms[0].id,
              item
            )
          }}
        >
          Добавить в корзину
        </Button>
      )}
    </div>
  )
}

export default AddRoomToCart