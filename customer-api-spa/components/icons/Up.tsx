import React from 'react'
import { SvgIcon } from '@material-ui/core'
import { SvgIconProps } from '@material-ui/core/SvgIcon/SvgIcon'

const starIcon: React.FC<SvgIconProps> = (props: SvgIconProps) => (<SvgIcon {...props}>
  <g fill="none" stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeWidth="14">
    <path transform="scale(.78125)" d="m64 41.5l45 45"/>
    <path transform="scale(.78125)" d="m64 41.5l-45 45"/>
  </g>
</SvgIcon>)

export default starIcon
