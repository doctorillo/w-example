import React from 'react'
import { SvgIcon } from '@material-ui/core'
import { SvgIconProps } from '@material-ui/core/SvgIcon/SvgIcon'

const starIcon: React.FC<SvgIconProps> = (props: SvgIconProps) => (<SvgIcon {...props}>
  <path d="m2.5 29.102c0-1.3984 0.5-2.8984 1.6016-4 2.1992-2.1992 5.6992-2.1992 7.8984 0l38 37.898 37.898-37.898c2.1992-2.1992 5.6992-2.1992 7.8984 0 2.1992 2.1992 2.1992 5.6992 0 7.8984l-41.797 41.898c-1.1016 1.1016-2.5 1.6016-4 1.6016s-2.8984-0.60156-4-1.6016l-41.898-41.898c-1.1016-1.1016-1.6016-2.5-1.6016-3.8984z"/>
</SvgIcon>)

export default starIcon
