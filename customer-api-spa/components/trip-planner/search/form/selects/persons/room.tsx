import React, { Fragment } from 'react'
import { ClientGroupItem } from '../../../../../../types/bookings/ClientGroupItem'
import { Nullable } from '../../../../../../types/Nullable'
import { QueryRoom, roomAdults, roomChildren } from '../../../../../../types/bookings/QueryRoom'
import { QueryGuest } from '../../../../../../types/bookings/QueryGuest'

export interface RoomSelect {
  category: ClientGroupItem;
  room: QueryRoom;
  addClient(
    roomPosition: number,
    age: Nullable<number>
  ): void;
  removeClient(
    roomPosition: number,
    clientPosition: number
  ): void;
  updateAgeClient(
    roomPosition: number,
    clientPosition: number,
    age: Nullable<number>
  ): void;
}

const ages = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]

function Room(props: RoomSelect) {
  const {
    category,
    room,
    addClient,
    removeClient,
    updateAgeClient,
  } = props
  const adultsCount: number = roomAdults(room)
  const childrenCount: number = roomChildren(room)
  const lastGuestPosition: number = room.guests.length === 0 ? 0 : room.guests[room.guests.length - 1].position
  return (
    <Fragment>
      <div className={'title'}>{room.position + 1} номер</div>
      <div
        className={'minus'}
        onClick={() =>
          adultsCount > 0 &&
          removeClient(
            room.position,
            lastGuestPosition
          )
        }>-</div>
      <div className={'info'}>
        {adultsCount} {adultsCount === 1 ? 'взрослый' : 'взрослых'}
      </div>
      <div className={'plus'} onClick={() => addClient(
        room.position,
        lastGuestPosition + 1
      )}>
        +
      </div>
      {category === ClientGroupItem.Family && (
        <React.Fragment>
          <div className={'minus'} onClick={() => removeClient(
            room.position,
            lastGuestPosition
          )}>
            -
          </div>
          <div className={'info'}>
            {childrenCount}
            {childrenCount === 1 ? ' ребенок' : ' детей'}
          </div>
          <div className={'plus'} onClick={() => addClient(
            room.position,
            3
          )}>
            +
          </div>
        </React.Fragment>
      )}
      {childrenCount > 0 && category === ClientGroupItem.Family && (
        <div className={'age'}>
          {room.guests.filter(x => x.age && x.age < 14).map((x: QueryGuest) => (
            <select
              key={`ch_${x.position}`}
              value={x.age ? x.age : undefined}
              onChange={e => {
                const age: Nullable<number> = parseInt(e.target.value, 10)
                age && updateAgeClient(
                  room.position,
                  x.position,
                  age
                )
              }}
            >
              {ages.map((z: number) => (
                <option key={z}>{z === 0 ? '< 1' : z}</option>
              ))}
            </select>
          ))}
        </div>
      )}
    </Fragment>
  )
}

export default Room
