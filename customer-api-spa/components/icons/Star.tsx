import React from 'react'
import { SvgIcon } from '@material-ui/core'
import { SvgIconProps } from '@material-ui/core/SvgIcon/SvgIcon'

const starIcon: React.FC<SvgIconProps> = (props: SvgIconProps) =>  (<SvgIcon {...props}>
  <path d="m61.055 40.418c-0.33984 0-0.64063-0.21875-0.74219-0.54297l-10.242-31.734-10.25 31.738c-0.10547 0.32422-0.40625 0.54297-0.74219 0.54297l-33.348-0.070313 27.02 19.559c0.27344 0.19922 0.39062 0.55469 0.28516 0.875l-10.371 31.699 26.945-19.656c0.27344-0.19922 0.64844-0.19922 0.92188 0l26.945 19.656-10.371-31.699c-0.10547-0.32422 0.007812-0.67578 0.28516-0.875l27.02-19.559-33.352 0.070313c0-0.003906-0.003906-0.003906-0.003906-0.003906z"/>
</SvgIcon>)

export default starIcon
