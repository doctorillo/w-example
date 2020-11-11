import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Table from '@material-ui/core/Table'
import Grid from '@material-ui/core/Grid'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableRow from '@material-ui/core/TableRow'
import ButtonGroup from '@material-ui/core/ButtonGroup'
import Button from '@material-ui/core/Button'
import format from 'date-fns/format'
import ru from 'date-fns/locale/ru/index'
import { plannerBasicSessionAsk, PlannerSession } from '../../../../types/planner/PlannerSession'
import Delete from '@material-ui/icons/Delete'
import { parseLocalDate, parseLocalDateTime } from '../../../../types/DateRange'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'

const useStyles = makeStyles(() => ({
  root: {
    width: '100%',
    overflowX: 'auto',
    background: 'white',
    marginTop: '1rem',
  },
  table: {
    minWidth: 650,
  },
}))

const tripHistory: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const styles = useStyles()
  if (!props.env || props.env.others.length === 0){
    return null
  }
  const { fn: {selectPlan, removePlan}, env: { others } } = props
  return (
    <Grid container spacing={2} direction="row" className={styles.root}>
      <Grid item xs={12} container justify="center" alignItems="center">
        {/*<Control {...props} />*/}
      </Grid>
      <Grid item xs={12} container>
        <Table size="small" className={styles.table}>
          <TableBody>
            {others.map((x: PlannerSession, idx: number) => {
              const ask = plannerBasicSessionAsk(x)
              if (!ask){
                return <React.Fragment key={idx} />
              }
              return (
                <TableRow key={idx}>
                  <TableCell>{idx + 1}</TableCell>
                  <TableCell>
                    {ask.session.identCode}
                  </TableCell>
                  <TableCell>
                    {ask.session.customerName}
                  </TableCell>
                  <TableCell>
                    {ask.point.point.label}
                  </TableCell>
                  <TableCell>
                    {format(
                      parseLocalDate(ask.point.dates.from),
                      'd MMM, EEEEEE',
                      { locale: ru },
                    )}
                  </TableCell>
                  <TableCell>
                    {format(
                      parseLocalDate(ask.point.dates.to),
                      'd MMM, EEEEEE',
                      {
                        locale: ru,
                      },
                    )}
                  </TableCell>
                  <TableCell>
                    {x.clients.length} чел.
                  </TableCell>
                  <TableCell>
                    {format(parseLocalDateTime(x.updated), 'dd.MM HH:ss', { locale: ru })}
                  </TableCell>
                  <TableCell>
                    <ButtonGroup size="small" color="secondary">
                      <Button
                        variant="contained"
                        onClick={() => removePlan(x.id)}
                      >
                        <Delete/>
                      </Button>
                      <Button onClick={() => selectPlan(x.id)}>
                        Выбрать
                      </Button>
                    </ButtonGroup>
                  </TableCell>
                </TableRow>
              )
            })}
          </TableBody>
        </Table>
      </Grid>
    </Grid>
  )
}

export default tripHistory
