import React, { Fragment } from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Fab from '@material-ui/core/Fab'
import Up from '@material-ui/icons/ArrowUpward'

const useStyles = makeStyles(theme => ({
  fab: {
    position: 'fixed',
    right: '.5rem',
    bottom: '.5rem',
  },
  extendedIcon: {
    margin: theme.spacing(1),
  },
}))

function ToTopButton (props: { href: string; visible: boolean }) {
  const { href, visible } = props
  const styles = useStyles()
  return !visible ? (
    <Fragment/>
  ) : (
    <Fab
      href={href}
      variant={'round'}
      color="secondary"
      size={'small'}
      className={styles.fab}
      aria-label="Вернуться к началу"
    >
      <Up className={styles.extendedIcon}/>
    </Fab>
  )
}

export default ToTopButton
