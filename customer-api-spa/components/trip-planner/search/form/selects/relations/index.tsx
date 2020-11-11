import React, { Fragment, useEffect } from 'react'
import connect, { RelationPageProps } from '../../../../../../redux/modules/relations/connect'
import { ResultKind } from '../../../../../../types/ResultKind'
import { FormControl } from '@material-ui/core'
import InputLabel from '@material-ui/core/InputLabel'
import NativeSelect from '@material-ui/core/NativeSelect'
import { Nullable } from '../../../../../../types/Nullable'
import { PartyValue } from '../../../../../../types/parties/PartyValue'
import { ContextItem } from '../../../../../../types/basic/ContextItem'

export interface RelationSelectProps {
  selectedParty: Nullable<PartyValue>;

  selectParty(party: Nullable<PartyValue>): void;
}

const selectRelation: React.FC<RelationPageProps> = (props: RelationPageProps) => {
  useEffect(() => {
    if (props.status === ResultKind.Undefined) {
      props.appProps.appEnv?.workspace?.businessPartyId && props.fn.fetch(props.appProps.appEnv.workspace.businessPartyId, ContextItem.Accommodation)
    }
  }, [props.status])
  if (!props.env) {
    return null
  }
  const { customers } = props.env
  const { selectedParty, selectParty } = props.appProps
  return <FormControl>
    <InputLabel htmlFor={'agents-native'}>Агент</InputLabel>
    <NativeSelect
      inputProps={{
        name: 'agents',
        id: 'agents-native',
      }}
      value={selectedParty?.id}
      onChange={(event) => {
        const id = event.target.value ? event.target.value : null
        if (!id) {
          selectParty(null)
        } else {
          const p = customers.find((x: PartyValue) => x.id === id)
          if (p) {
            selectParty(p)
          }
        }
      }}>
      <Fragment>
        <option value=""/>
        {customers.map((x: PartyValue, idx: number) => <option key={idx} value={x.id}>
          {x.name}
        </option>)}
      </Fragment>
    </NativeSelect>
  </FormControl>
}

export default connect(selectRelation)