import React, { Fragment } from 'react'
import Link from 'next/link'
import numeral from 'numeral'
import { formatShortLocalDate, parseLocalDate } from '../../../../types/DateRange'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import { Uuid } from '../../../../types/basic/Uuid'
import { PlannerExcursionVariantDate } from '../../../../types/planner/PlannerExcursionVariantDate'
import { PlannerExcursionItem } from '../../../../types/planner/PlannerExcursionItem'

export interface PlannerViewExcursionVariantItem {
  idx: number;
  plannerId: string;
  plannerPointId: string;
  plannerClients: PlannerClient[];
  item: PlannerExcursionVariantDate;

  removeExcursionVariant(plannerId: Uuid,
                         plannerPointId: Uuid,
                         plannerClientId: Uuid[],
                         itemId: Uuid): void;
}

const plannerExcursionVariant: React.FC<PlannerViewExcursionVariantItem> = (props: PlannerViewExcursionVariantItem) => {
  const {
    plannerId,
    plannerPointId,
    item,
    removeExcursionVariant,
  } = props
  return (
    <Fragment>
      <div className={'title'}>
        {formatShortLocalDate(parseLocalDate(item.date))}
      </div>
      {item.variants.map((x: PlannerExcursionItem, idx) => {
          return ([<div key={'title'} className={'item'}>
            <div className={'title'}>
              {idx + 1}. {x.excursionName}
            </div>
            <div className={'price'}>
              {numeral(Math.round(x.total.value)).format(',')} €
            </div>
          </div>, ...x.clients.map((y, idy) => (<div className={'description'} key={y.clientId}>
            <div className={'room'}>
              {idy + 1} турист
            </div>
            <div className={'group'}>
              <div
                onClick={() =>
                  removeExcursionVariant(plannerId,
                    plannerPointId,
                    [y.clientId],
                    x.id,
                  )
                }
              >
                <i className={`material-icons`}>remove_circle_outline</i>
              </div>
              <Link href={'/property'}>
                <div onClick={() => console.log('click')}>
                  <i className={`material-icons`}>arrow_forward</i>
                </div>
              </Link>
            </div>
          </div>))])
        },
      )}
    </Fragment>
  )
}

export default plannerExcursionVariant