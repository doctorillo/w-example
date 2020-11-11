import React from 'react'
import useStyles from './styles'
import { ClientGroupItem } from '../../../../../../types/bookings/ClientGroupItem'
import { QueryGroup } from '../../../../../../types/bookings/QueryGroup'
import Room from './room'
import { QueryRoom, roomInit } from '../../../../../../types/bookings/QueryRoom'
import { v4 as uuid } from 'uuid'
import { Nullable } from '../../../../../../types/Nullable'
import { guestInit } from '../../../../../../types/bookings/QueryGuest'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../../theme'
import clsx from 'clsx'

export interface PersonSelect {
  queryGroup: QueryGroup;
  top: number;
  left: number;
  update(queryGroup: QueryGroup): void;
  toggle(): void;
}

function useModal(props: PersonSelect) {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  const {
    top,
    left,
    queryGroup,
    toggle,
    update,
  } = props
  const twoPane =
    queryGroup.category !== ClientGroupItem.Solo &&
    queryGroup.category !== ClientGroupItem.Duo

  const minusRoom = () => update({ ...queryGroup, rooms: queryGroup.rooms.slice(0, queryGroup.rooms.length - 1) })

  const plusRoom = () => {
    const x = queryGroup.rooms.length === 0 ? roomInit(0, 1) : roomInit(queryGroup.rooms[queryGroup.rooms.length - 1].position + 1, 1)
    return update({ ...queryGroup, rooms: [...queryGroup.rooms, x] })
  }

  const addClient = (roomPosition: number, age: Nullable<number>): void => {
    const rooms = queryGroup.rooms.map(z => {
      if (z.position === roomPosition) {
        return ({ ...z, guests: [...z.guests, guestInit(age, null, z.guests.length)] })
      } else {
        return z
      }
    })
    update({ ...queryGroup, rooms })
  }

  const removeClient = (roomPosition: number, clientPosition: number): void => {
    const rooms = queryGroup.rooms.map(z => {
      if (z.position === roomPosition) {
        return ({ ...z, guests: z.guests.filter(y => y.position !== clientPosition) })
      } else {
        return z
      }
    })
    update({ ...queryGroup, rooms })
  }

  const updateAgeClient = (
    roomPosition: number,
    clientPosition: number,
    age: Nullable<number>,
  ): void => {
    const rooms = queryGroup.rooms.map(z => {
      if (z.position === roomPosition) {
        const guests = z.guests.map(y => {
          if (y.position === clientPosition){
            return {...y, age }
          } else {
            return y
          }
        })
        return ({ ...z, guests })
      } else {
        return z
      }
    })
    update({ ...queryGroup, rooms })
  }
  return (<div
      className={clsx({
        [style.root]: true,
        [style.normal]: !twoPane,
        [style.wide]: twoPane,
      })}
      style={{ top: top - 12, left: twoPane ? left - 106 : left - 42 }}
    >
      <div
        className={clsx({
          left: true,
          full: !twoPane,
          half: twoPane,
        })}
      >
        <div
          className={clsx({
            item: true,
            active: queryGroup.category === ClientGroupItem.Solo,
          })}
          onClick={() => {
            update({ id: uuid(), category: ClientGroupItem.Solo, rooms: [roomInit(0, 1)] })
            toggle()
          }}
        >
          1 взрослый, 1 номер
        </div>
        <div
          className={clsx({
            item: true,
            active: queryGroup.category === ClientGroupItem.Duo,
          })}
          onClick={() => {
            queryGroup.category !== ClientGroupItem.Duo && update({
              id: uuid(),
              category: ClientGroupItem.Duo,
              rooms: [roomInit(0, 2)],
            })
            toggle()
          }}
        >
          2-e взрослых, 1 номер
        </div>
        <div
          className={clsx({
            item: true,
            active: queryGroup.category === ClientGroupItem.Family,
          })}
          onClick={() => {
            queryGroup.category !== ClientGroupItem.Family && update({
              id: uuid(),
              category: ClientGroupItem.Family,
              rooms: [roomInit(0, 2, 1)],
            })
            toggle()
          }}
        >
          Семьей
        </div>
        <div
          className={clsx({
            item: true,
            active: queryGroup.category === ClientGroupItem.Group,
          })}
          onClick={() => {
            queryGroup.category !== ClientGroupItem.Group && update({
              id: uuid(),
              category: ClientGroupItem.Group,
              rooms: [roomInit(0, 2)],
            })
          }}
        >
          Группой
        </div>
      </div>
      {twoPane && (
        <div className={'right'}>
          <div className={'minus'} onClick={minusRoom}>
            -
          </div>
          <div className={'info'}>Номера</div>
          <div className={'plus'} onClick={plusRoom}>
            +
          </div>
          {queryGroup.rooms.map((x: QueryRoom) => (<Room
            key={x.position}
            room={x}
            category={queryGroup.category}
            addClient={addClient}
            removeClient={removeClient}
            updateAgeClient={updateAgeClient}
          />))}
          <div className={'action'}>
            <button
              className={'button'}
              onClick={() => {
                toggle()
              }}>Закрыть
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default useModal