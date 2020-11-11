import React from 'react'
import { IconProps } from './IconProps'

function HamburgerIcon(props: IconProps) {
  return (
    <svg
      width={props.width}
      height={props.height}
      fill={props.color}
      fillRule="evenodd"
      viewBox="0 0 100 100"
      xmlns="http://www.w3.org/2000/svg"
    >
      <g fill="none" stroke="#000" strokeLinecap="square">
        <path
          d="M18.75 72.917h62.5M18.75 27.082h62.5M18.75 50h62.5"
          strokeWidth="4.1667"
        />
      </g>
    </svg>
  )
}

export default HamburgerIcon
