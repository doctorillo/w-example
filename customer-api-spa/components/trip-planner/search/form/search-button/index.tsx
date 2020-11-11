import React, { forwardRef, LegacyRef } from 'react'
import useStyles from './styles'
import clsx from 'clsx'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../../theme'

export type ButtonSearchProps = {
  disabled: boolean;
  label: string;
  onClick: () => void;
}

const SearchButton = (props: ButtonSearchProps & {ref: LegacyRef<any>}) => {
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  return (<button
    ref={props.ref}
    onClick={props.onClick}
    className={clsx({
      [style.root]: !props.disabled,
      [style.disabled]: props.disabled,
    })}
  >
    {props.label}
  </button>)
}

const buttonSearch = forwardRef(SearchButton)

export default buttonSearch
