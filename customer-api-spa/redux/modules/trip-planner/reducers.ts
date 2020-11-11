import {
  PayloadPlannerClean,
  PayloadPlannerCreated,
  PayloadPlannerCreateFormView,
  PayloadPlannerFetchCompleted,
  PayloadPlannerRemoved,
  PayloadPlannerSelected,
  PayloadPlannerToggle,
  PayloadPlannerUpdated,
  plannerClean,
  plannerCreated,
  plannerCreateFormView,
  plannerFetchCompleted,
  plannerRemoved,
  plannerSelected,
  plannerToggle,
  plannerSetBookingStep,
  plannerUpdated, PayloadPlannerSetBookingStep,
} from './actions'
import { PlannerSession } from '../../../types/planner/PlannerSession'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { cleanDataState, DataState, fulfillDataState, initDataState } from '../../../types/stores/DataState'
import { Nullable } from '../../../types/Nullable'
import { Uuid } from '../../../types/basic/Uuid'
import { parseLocalDateTime } from '../../../types/DateRange'
import { Sort } from '../../../types/stores/Sort'

export type STATE_TRIP_PLANNER_ENV = { selected: Nullable<Uuid>; create: boolean; preview: boolean }
export type STATE_TRIP_PLANNER = DataState<PlannerSession, STATE_TRIP_PLANNER_ENV>

export const sortPlannerSessionFn: Sort<PlannerSession> = (x: PlannerSession, y: PlannerSession) => parseLocalDateTime(y.updated).getTime() - parseLocalDateTime(x.updated).getTime()

const cleanR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerClean> = state => cleanDataState(state)

const createFormViewR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerCreateFormView> = (
  state,
  action,
) => {
  const extraEnv = state.extraEnv ? { ...state.extraEnv, create: action.payload, preview: false } : { selected: null, create: action.payload, preview: false }
  return { ...state, extraEnv }
}

const completedR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerFetchCompleted> = (
  state,
  action,
) => {
  const items = action.payload.sort(sortPlannerSessionFn)
  const extraEnv = { selected: null, create: false, preview: false }
  return { ...fulfillDataState(state, items, null), extraEnv }
}

const createdR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerCreated> = (
  state,
  action,
) => {
  const items = ([action.payload, ...state.internal.filter(x => x.id !== action.payload.id)]).sort(sortPlannerSessionFn)
  const extraEnv = { selected: action.payload.id, create: true, preview: false }
  return { ...fulfillDataState(state, items, null), extraEnv }
}

const removedR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerRemoved> = (
  state,
  action,
) => {
  const items = state.internal.filter(x => x.id !== action.payload)
  return fulfillDataState(state, items, null)
}

const updatedR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerUpdated> = (
  state,
  action,
) => {
  const items = ([action.payload, ...state.internal.filter(x => x.id !== action.payload.id)]).sort(sortPlannerSessionFn)
  return fulfillDataState(state, items, null)
}

const selectedR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerSelected> = (
  state,
  action,
) => {
  const extraEnv = {...state.extraEnv, selected: action.payload}
  return {...state, extraEnv}
}

const toggleR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerToggle> = (
  state,
  action,
) => {
  const extraEnv = {...state.extraEnv, preview: action.payload}
  return {...state, extraEnv}
}

const bookingStepR: CaseReducer<STATE_TRIP_PLANNER, PayloadPlannerSetBookingStep> = (
  state,
  action,
) => {
  const selected = state.extraEnv?.selected || null
  if (!selected){
    return state
  }
  const items = state.internal.map(x => {
    if (x.id !== selected){
      return x
    }
    return {...x, bookingStep: action.payload}
  })
  return fulfillDataState(state, items, null)
}

export const reducerTripPlanner = createReducer<STATE_TRIP_PLANNER>(initDataState<PlannerSession, STATE_TRIP_PLANNER_ENV>({
  selected: null, create: false, preview: false
  }), builder => builder
  .addCase(plannerClean, cleanR)
  .addCase(plannerCreateFormView, createFormViewR)
  .addCase(plannerFetchCompleted, completedR)
  .addCase(plannerCreated, createdR)
  .addCase(plannerRemoved, removedR)
  .addCase(plannerUpdated, updatedR)
  .addCase(plannerSelected, selectedR)
  .addCase(plannerToggle, toggleR)
  .addCase(plannerSetBookingStep, bookingStepR)
)
