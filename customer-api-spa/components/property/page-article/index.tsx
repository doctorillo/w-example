import React from 'react'
import useStyles from './styles'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../theme'

export type ArticleProps = {
  anchor: string;
  header: string;
  subHeader: string | null;
}
const propertyPageArticle: React.FC<ArticleProps> = props => {
  const { anchor, header, subHeader, children } = props
  const theme = useTheme<AppTheme>()
  const style = useStyles(theme)
  return (<div
    id={anchor}
    className={style.root}
  >
    <h3 className={'header'}>{header}</h3>
    {subHeader && <h6 className={'header'}>{subHeader}</h6>}
    <div className={'text'}>
      {children}
    </div>
  </div>)
}

export default propertyPageArticle