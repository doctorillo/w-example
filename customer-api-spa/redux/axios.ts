import Fingerprint2 from 'fingerprintjs2'
import { from, Observable, of } from 'rxjs'
import { catchError } from 'rxjs/operators'
import Axios from 'axios'
import { QueryResult } from '../types/QueryResult'
import {v4 as uuidv4} from 'uuid'

export const baseUrl: string = process.env.NODE_ENV === 'production' ? 'https://alfa.veditour.com' : 'http://localhost:3000'

const axiosCreate = (isProduction: boolean) =>
  Axios.create({
    withCredentials: false,
    baseURL: isProduction
      ? `${baseUrl}/xhr/`
      : `http://127.0.0.1:7001/`,
    timeout: 5000,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Credentials': true,
      'Access-Control-Allow-Origin': 'http://localhost:3000',
    },
  })

const axios = axiosCreate(process.env.NODE_ENV === 'production')

const sessionHeader = 'x-session-id'
const fingerprintHeader = 'x-request-id'

if (!axios.defaults.headers.common[sessionHeader]) {
  // console.log('session plannerCreate')
  axios.defaults.headers.common[sessionHeader] = uuidv4()
}
if (!axios.defaults.headers.common[fingerprintHeader]) {
  Fingerprint2.get(function(components) {
    axios.defaults.headers.common[fingerprintHeader] = Fingerprint2.x64hash128(
      components
        .map(function(pair) {
          return pair.value
        })
        .join(),
      31,
    )
  })
}

export function axiosGet$<Result>(path: string): Observable<QueryResult<Result>> {
  return from(axios
    .get<QueryResult<Result>>(path)
    .then(x => x.data)).pipe(
    catchError(x => of({
      items: [],
      size: 0,
      hasError: true,
      debug: [`${x}`],
    })))
}

export function axiosPost$<QueryParam, Result>(path: string, data: QueryParam): Observable<QueryResult<Result>> {
  return from(axios
    .post<QueryResult<Result>>(path, JSON.stringify(data)).then(x => x.data)).pipe(
    catchError(x => of({
      items: [],
      size: 0,
      hasError: true,
      debug: [`${x}`],
    })))
}
