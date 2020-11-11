import React from 'react'
import { SvgIcon } from '@material-ui/core'
import { SvgIconProps } from '@material-ui/core/SvgIcon/SvgIcon'

const guestIcon: React.FC<SvgIconProps> = (props: SvgIconProps) => (<SvgIcon {...props}>
  <g>
    <path
      d="m95 33h-5.1016c1.3008-1.3008 2.1016-3 2.1016-5 0-3.8984-3.1016-7-7-7s-7 3.1016-7 7c0 2 0.80078 3.6992 2.1016 5h-5.1016v30h3v16h14v-16h3zm-15-5c0-2.8008 2.1992-5 5-5s5 2.1992 5 5-2.1992 5-5 5-5-2.1992-5-5zm10 49h-10v-14h10zm3-16h-16v-26h16z"/>
    <path
      d="m25 33h-5.1016c1.3008-1.3008 2.1016-3 2.1016-5 0-3.8984-3.1016-7-7-7s-7 3.1016-7 7c0 2 0.80078 3.6992 2.1016 5h-5.1016v30h3v16h14v-16h3zm-15-5c0-2.8008 2.1992-5 5-5s5 2.1992 5 5-2.1992 5-5 5-5-2.1992-5-5zm10 49h-10v-14h10zm3-16h-16v-26h16z"/>
    <path
      d="m51 33h-5.1016c1.3008-1.3008 2.1016-3 2.1016-5 0-3.8984-3.1016-7-7-7s-7 3.1016-7 7c0 2 0.80078 3.6992 2.1016 5h-5.1016v30h3v16h14v-16h3zm-15-5c0-2.8008 2.1992-5 5-5s5 2.1992 5 5-2.1992 5-5 5-5-2.1992-5-5zm10 49h-10v-14h10zm3-16h-16v-26h16z"/>
    <path d="m71 49h-7v-7h-2v7h-7v2h7v7h2v-7h7z"/>
  </g>
</SvgIcon>)

export default guestIcon
