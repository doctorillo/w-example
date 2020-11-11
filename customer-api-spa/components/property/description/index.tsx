import React from 'react'
import Article from '../page-article'

function PropertyDescription(props: { text: string }) {
  return (<Article anchor={props.text} header="Описание" subHeader={null} />)
}

export default PropertyDescription
