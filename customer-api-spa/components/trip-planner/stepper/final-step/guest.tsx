import React from 'react'
import useStyles from './guest-styles'
import { useTheme } from '@material-ui/core'
import { AppTheme } from '../../../theme'
import { TripPlannerPageProps } from '../../../../redux/modules/trip-planner/TripPlannerPageProps'
import { PlannerClient } from '../../../../types/planner/PlannerClient'
import format from 'date-fns/format'
import parseISO from 'date-fns/parseISO'
import ru from 'date-fns/locale/ru'
import { GenderItem } from '../../../../types/parties/GenderItem'


const orderGuest: React.FC<TripPlannerPageProps> = (props: TripPlannerPageProps) => {
  const theme = useTheme<AppTheme>()
  const styles = useStyles(theme)
  if (!props.env || !props.env.basic) {
    return null
  }
  const { env: { basic: { plannerClients } } } = props
  return (<div className={styles.root}>
    <div className={'title'}>
      Туристы
    </div>
    {plannerClients.map((x: PlannerClient, idx: number) => {
      const { meta } = x
      const firstName = meta && meta.firstName
      const lastName = meta && meta.lastName
      const birthDay = meta && meta.birthDay && format(parseISO(meta.birthDay), 'dd.MM.yyyy', { locale: ru })
      const gender = !meta ? undefined : meta.gender === GenderItem.Male ? 'Мужчина' : 'Женщина'
      const passportSerial = (meta && meta.passport && meta.passport.serial) || ''
      const passportNumber = (meta && meta.passport && meta.passport.number && ` ${meta.passport.number}`) || ''
      const passportExpired = (meta && meta.passport && meta.passport.expiredAt && format(parseISO(meta.passport.expiredAt), 'dd.MM.yyyy', { locale: ru })) || ''
      const passportState = (meta && meta.passport && meta.passport.state) || ''
      const passport = `${passportSerial}${passportNumber}`
      return (<div key={idx} className={styles.tourist}>
        <div className={'idx'}>
          {idx + 1}
        </div>
        {(firstName || lastName) && <div className={'content'}>
          <div className={'label'}>
            Имя:
          </div>
          {firstName}{' '}{lastName}
        </div>}
        {birthDay && <div className={'content'}>
          <div className={'label'}>
            Дата рождения
          </div>
          {birthDay}
        </div>}
        {(firstName || lastName) && <div className={'content'}>
          <div className={'label'}>
            Пол
          </div>
          {gender}
        </div>}
        {passport && <div className={'content'}>
          <div className={'label'}>
            Паспорт
          </div>
          {passport}
        </div>}
        {passportExpired && <div className={'content'}>
          <div className={'label'}>
            Срок действия паспорта
          </div>
          {passportExpired}
        </div>}
        {passportState && <div className={'content'}>
          <div className={'label'}>
            Гражданство
          </div>
          {passportState}
        </div>}
      </div>)
    })}</div>)
}

export default orderGuest