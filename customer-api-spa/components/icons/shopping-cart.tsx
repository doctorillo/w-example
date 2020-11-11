import React from 'react'
import { IconProps } from './IconProps'

function ShoppingCartIcon(props: IconProps) {
  return (
    <svg
      width={props.width}
      height={props.height}
      fill={props.color}
      viewBox="0 0 100 100"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M8.012 10a2 2 0 1 0 0 4h10.375l10.53 49.75c.255 1.23 1.079 2.262 2.094 2.25h50c1.055.016 2.028-.941 2.028-2s-.973-2.016-2.028-2H32.637l-1.688-8h54.062c.895-.012 1.742-.688 1.938-1.563l7-30c.261-1.16-.747-2.43-1.938-2.437h-68.25l-1.781-8.438c-.2-.882-1.063-1.57-1.969-1.562zm16.594 14h17.719l1.843 11H26.95zm21.75 0h21.312l-1.844 11H48.199zM71.7 24h17.812l-2.563 11H69.855zM27.794 39h17.03l1.845 11H30.107zm21.094 0h16.25l-1.813 11H50.7zM69.2 39h16.812l-2.594 11H67.356zM41.012 72c-4.946 0-9 4.055-9 9s4.054 9 9 9c4.945 0 9-4.055 9-9s-4.055-9-9-9zm30 0c-4.946 0-9 4.055-9 9s4.054 9 9 9c4.945 0 9-4.055 9-9s-4.055-9-9-9zm-30 4c2.785 0 5 2.215 5 5s-2.215 5-5 5-5-2.215-5-5 2.215-5 5-5zm30 0c2.785 0 5 2.215 5 5s-2.215 5-5 5-5-2.215-5-5 2.215-5 5-5z"/>
    </svg>
  )
}

export default ShoppingCartIcon