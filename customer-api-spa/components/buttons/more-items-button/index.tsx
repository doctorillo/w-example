import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Button from '@material-ui/core/Button'
import Refresh from '@material-ui/icons/RefreshOutlined'

const useStyles = makeStyles(theme => ({
  margin: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  extendedIcon: {
    marginLeft: theme.spacing(1),
  },
}))

export default function MoreItems(props: {
  maxItems: number;
  filterMaxItems: (max: number) => void;
}) {
  const { maxItems, filterMaxItems } = props
  const styles = useStyles()
  return (<Button
    variant="outlined"
    size="small"
    color="secondary"
    aria-label="Загрузить еще"
    className={styles.margin}
    onClick={() => filterMaxItems(maxItems + 10)}
  >
    Загрузить еще <Refresh className={styles.extendedIcon} />
  </Button>)
}
