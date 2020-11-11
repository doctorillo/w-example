import React from 'react'
import { SearchPropertyProps } from '../../../../redux/modules/property-search/connect'

// const ctx = classBind.bind(style)

const filterIndications: React.FC<SearchPropertyProps> = (props: SearchPropertyProps) => {
  if (!props.env){
    return null
  }
  return <div />
  /*const { filterParams: { indications }, filterIndications } = props.searchPropertyProps
  const [open, setOpen] = useState(true)
  const [edited, setEdited] = useState(indications)
  const [debouncedCallback] = useDebouncedCallback(
    (value) => {
      filterIndications(value)
    },
    1000,
  )
  return (<div className={style.block}>
    <div className={ctx({
      label: true,
      excursionSelectedDates: open,
    })} onClick={() => setOpen(!open)}>
      Показания к лечению
    </div>
    <div className={style.toggle} onClick={() => setOpen(!open)}>
      {!open && <Down width={'1em'} height={'1em'} fill={'#b9c0cd'}/>}
      {open && <Up width={'1em'} height={'1em'} fill={'#b9c0cd'}/>}
    </div>
    {open && <div className={style.filter}>
    </div>}
  </div>)*/
}

export default filterIndications