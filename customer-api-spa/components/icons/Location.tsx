import React from 'react'
import { SvgIcon } from '@material-ui/core'
import { SvgIconProps } from '@material-ui/core/SvgIcon/SvgIcon'

const locationIcon: React.FC<SvgIconProps> = (props: SvgIconProps) =>  (<SvgIcon {...props}>
  <path d="m50 7.0039c-16.043 0-29 13.527-29 30 0 6.2305 2.3516 13.625 5.2188 18.5l21.188 36c0.53516 0.92188 1.5234 1.4922 2.5938 1.4922s2.0586-0.57031 2.5938-1.4922l21.188-36c2.8672-4.875 5.2188-12.27 5.2188-18.5 0-16.473-12.957-30-29-30zm0 6c12.676 0 23 10.648 23 24 0 4.5156-2.2539 11.809-4.4062 15.469l-18.594 31.594-18.594-31.594c-2.1523-3.6602-4.4062-10.953-4.4062-15.469 0-13.352 10.324-24 23-24zm0 8c-8.25 0-15 6.75-15 15s6.75 15 15 15 15-6.75 15-15-6.75-15-15-15zm0 6c5.0078 0 9 3.9922 9 9 0 5.0078-3.9922 9-9 9s-9-3.9922-9-9c0-5.0078 3.9922-9 9-9z"/>
</SvgIcon>)

export default locationIcon
