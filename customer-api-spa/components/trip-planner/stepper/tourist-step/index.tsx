import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import TextField from '@material-ui/core/TextField'
import Radio from '@material-ui/core/Radio'
import RadioGroup from '@material-ui/core/RadioGroup'
import Grid from '@material-ui/core/Grid'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import FormControl from '@material-ui/core/FormControl'
import FormLabel from '@material-ui/core/FormLabel'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import parse from 'date-fns/parseISO'
import isBefore from 'date-fns/isBefore'
import isAfter from 'date-fns/isAfter'
import { Nullable } from '../../../../types/Nullable'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'

const from: Date = parse('1919-01-01')

function isBirthDayValid(date: Nullable<string>): boolean {
  if (!date) {
    return false
  }
  const to = new Date()
  const parsed = parse(date)
  if (!parsed) {
    return false
  }
  return isAfter(parsed, from) && isBefore(parsed, to)
}

function isPasswordValid(lastDate: Nullable<string>, date: Nullable<string>): boolean {
  if (!lastDate || !date) {
    return false
  }
  const ld = parse(lastDate)
  const parsed = parse(date)
  if (!ld || !parsed) {
    return false
  }
  return isBefore(ld, parsed)
}

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexFlow: 'row wrap',
    padding: theme.spacing(1),
  },
  formControl: {
    display: 'flex',
    width: '30rem',
    minWidth: '30rem',
    margin: theme.spacing(1),
    padding: theme.spacing(1),
    border: '1px solid rgba(0, 0, 0, 0.1)',
    borderRadius: '2px',
  },
  field: {
    display: 'flex',
    margin: theme.spacing(1),
  },
  group: {
    display: 'flex',
    width: '100%',
    margin: theme.spacing(1, 1),
  },
}))

export default function TouristForm(props: TripPlannerPageProps) {
  if (!props.env || !props.env?.basic) {
    return null
  }
  const {
    basic: {
      plannerPoint: { dates },
      plannerClients,
    },
  } = props.env
  const {
    setFirstName,
    setLastName,
    setGender,
    setBirthDay,
    setPassportSerial,
    setPassportNumber,
    setPassportExpired,
    setPassportState,
  } = props.fn
  const styles = useStyles()
  const lastDate = dates && dates.to
  return (
    <div className={styles.root}>
      {plannerClients.map((x: PlannerClient, idx: number) => {
        const { id, meta } = x
        const firstName = meta.firstName
        const lastName = meta.lastName
        const birthDay = meta.birthDay
        const gender = !meta ? undefined : meta.gender
        const passportSerial = meta.passport.serial
        const passportNumber = meta.passport.number
        const passportExpiredAt = meta.passport.expiredAt
        const passportState = meta.passport.state
        return (
          <FormControl key={idx} component="fieldset" className={styles.formControl}>
            <FormLabel component="legend">{idx + 1} турист</FormLabel>
            <Grid container>
              <Grid item xs={6} container alignItems="flex-start">
                <TextField
                  required
                  margin="dense"
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Имя"
                  value={firstName}
                  onChange={e => setFirstName(id, e.target.value)}
                />
                <TextField
                  required
                  margin="dense"
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Фамилия"
                  value={lastName}
                  onChange={e => setLastName(id, e.target.value)}
                />
                <TextField
                  type="date"
                  margin="dense"
                  required
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Дата рождения"
                  value={birthDay}
                  onChange={e => {
                    isBirthDayValid(e.target.value) &&
                    setBirthDay(id, e.target.value)
                  }}
                />
                <RadioGroup
                  aria-label="gender"
                  name="gender"
                  //margin="dense"
                  className={styles.group}
                  value={`${gender}`}
                  onChange={(e, v) => {
                    e
                    const value = parseInt(v)
                    if (isNaN(value)) {
                      return
                    }
                    setGender(x.id, value)
                  }}
                >
                  <FormControlLabel
                    value="1"
                    control={<Radio color="primary"/>}
                    label="Женщина"
                    labelPlacement="end"
                  />
                  <FormControlLabel
                    value="0"
                    control={<Radio color="primary"/>}
                    label="Мужчина"
                    labelPlacement="end"
                  />
                </RadioGroup>
              </Grid>
              <Grid
                item
                xs={6}
                container
                direction="column"
                justify="flex-start"
                alignItems="flex-start"
              >
                <TextField
                  required
                  margin="dense"
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Серия паспорта"
                  value={passportSerial}
                  onChange={e => setPassportSerial(id, e.target.value)}
                />
                <TextField
                  required
                  margin="dense"
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Номер паспорта"
                  value={passportNumber}
                  onChange={e => setPassportNumber(id, e.target.value)}
                />
                <TextField
                  type="date"
                  required
                  margin="dense"
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Действителен до"
                  value={passportExpiredAt}
                  onChange={e => {
                    isPasswordValid(lastDate, e.target.value) &&
                    setPassportExpired(id, e.target.value)
                  }}
                />
                <TextField
                  required
                  margin="dense"
                  className={styles.field}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  variant="outlined"
                  label="Страна выдачи паспорта"
                  value={passportState}
                  onChange={e => setPassportState(id, e.target.value)}
                />
              </Grid>
            </Grid>
          </FormControl>
        )
      })}
    </div>
  )
}
