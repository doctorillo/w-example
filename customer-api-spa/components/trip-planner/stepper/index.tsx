import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Stepper from '@material-ui/core/Stepper'
import Step from '@material-ui/core/Step'
import StepLabel from '@material-ui/core/StepLabel'
import StepContent from '@material-ui/core/StepContent'
import Button from '@material-ui/core/Button'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'
import Box from '@material-ui/core/Box'
import TouristStep from './tourist-step'
import PropertyStep from './properties-step'
import ExcursionStep from './excursion-step'
import FinalStep from './final-step'
import { TripPlannerPageProps } from '../../../redux/modules/trip-planner/TripPlannerPageProps'

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexFlow: 'column wrap',
    alignSelf: 'center',
    alignContent: 'center',
    justify: 'center',
    maxWidth: '55rem',
    marginTop: theme.spacing(2),
  },
  button: {
    marginTop: theme.spacing(1),
    marginRight: theme.spacing(1),
  },
  actionsContainer: {
    marginBottom: theme.spacing(2),
    minWidth: '40rem',
  },
  resetContainer: {
    padding: theme.spacing(3),
  },
}))

function getSteps (hasProperty: boolean, hasExcursion: boolean): string[] {
  if (!hasProperty && !hasExcursion){
    return ['Туристы']
  }
  if (hasProperty && !hasExcursion){
    return ['Туристы', 'Проживание', 'Заявка']
  }
  if (!hasProperty && hasExcursion){
    return ['Туристы', 'Экскурсии', 'Заявка']
  }
  return ['Туристы', 'Проживание', 'Экскурсии', 'Заявка']
}

const tripPlannerMain: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  if (!props.env || !props.env.basic){
    return null
  }
  const { bookingStep } = props.env.basic
  const { plannerSetBookingStep } = props.fn
  const hasProperty = (props?.env?.property?.propertyVariantCount || 0) > 0
  const hasExcursion = (props?.env?.excursion?.excursionVariantCount || 0) > 0
  const styles = useStyles()
  const steps = getSteps(hasProperty, hasExcursion)
  const propertyIdx = hasProperty ? 1 : 0
  const excursionIdx = hasExcursion ? propertyIdx + 1 : propertyIdx
  const finalIdx = excursionIdx + 1
  return (<Paper className={styles.root}>
      <Box component="span" color="text.hint" textAlign="center" fontSize="2rem" fontWeight={300}>
        Создание заявки
      </Box>
      <Stepper activeStep={bookingStep} orientation="vertical">
        {steps.map((label, index) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
            <StepContent>
              {index === 0 && <TouristStep {...props} />}
              {index === propertyIdx && <PropertyStep {...props} />}
              {index === excursionIdx && <ExcursionStep {...props} />}
              {index === finalIdx && <FinalStep {...props} />}
              <div className={styles.actionsContainer}>
                <div>
                  <Button
                    disabled={bookingStep === 0}
                    className={styles.button}
                    onClick={() => plannerSetBookingStep(bookingStep -1)}
                  >
                    Назад
                  </Button>
                  <Button
                    variant="contained"
                    color="primary"
                    className={styles.button}
                    onClick={() => plannerSetBookingStep(bookingStep + 1)}
                  >
                    {bookingStep === steps.length - 1 ? 'Забронировать' : 'Далее'}
                  </Button>
                </div>
              </div>
            </StepContent>
          </Step>
        ))}
      </Stepper>
      {bookingStep === steps.length && (
        <Paper square elevation={0} className={styles.resetContainer}>
          <Typography>All steps completed - you&apos;re finished</Typography>
          <Button className={styles.button} onClick={() => plannerSetBookingStep(0)}>
            Reset
          </Button>
        </Paper>
      )}
    </Paper>
  )
}

export default tripPlannerMain