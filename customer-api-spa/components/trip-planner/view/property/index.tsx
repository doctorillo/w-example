import React, { Fragment } from 'react'
import Link from 'next/link'
import { PlannerRoom } from '../../../../types/planner/PlannerRoom'
import { PlannerAccommodationItem } from '../../../../types/planner/PlannerAccommodationItem'
import numeral from 'numeral'
import { PointPlanViewProps } from '../active-point'

const plannerPropertyView: React.FC<PointPlanViewProps> = (props: PointPlanViewProps) => {
  const {
    customerId,
    supplierId,
    plannerId,
    plannerPointId,
    plannerProperty,
    removeRoomVariant,
    toProperty,
  } = props
  return !plannerProperty ? null : (
    <Fragment>
      {plannerProperty?.propertyRooms.map((x: PlannerRoom) =>
        x.variants
          .filter(y => !y.markAsDelete)
          .map((y: PlannerAccommodationItem, idx: number) => (
            <Fragment key={y.id}>
              <div className={'item'}>
                <div className={'title'}>
                  {idx + 1}. {y.propertyName} {y.propertyStar}*
                </div>
                <div className={'price'}>
                  {numeral(Math.round(y.price.value)).format(',')} €
                </div>
              </div>
              <div className={'description'}>
                <div key={y.roomPriceUnitId} className={'room'}>
                  {y.roomType} {y.roomCategory} {y.boarding} {y.tariff}
                </div>
                <div className={'group'}>
                  <div
                    onClick={() =>
                      plannerProperty &&
                      removeRoomVariant(
                        plannerId,
                        plannerPointId,
                        x.id,
                        y.id,
                      )
                    }
                  >
                    <i className={`material-icons`}>remove_circle_outline</i>
                  </div>
                  <Link href={'/property'}>
                    <div onClick={() => toProperty(y.propertyId, supplierId, customerId)}>
                      <i className={`material-icons`}>arrow_forward</i>
                    </div>
                  </Link>
                </div>
              </div>
            </Fragment>
          )),
      )}
    </Fragment>
  )
}

export default plannerPropertyView