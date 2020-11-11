import React, { Fragment, ReactNode } from 'react'
import Head from 'next/head'
import TopMenu from '../top-menu'
import { AppProps } from '../../redux/modules/env/connect'

export interface AppLayoutProps {
  title: string;
  menuItems: any;
  appProps: AppProps;
}
const appLayout: React.FC<AppLayoutProps> = (props: AppLayoutProps & { children?: ReactNode }) => {
  const { title, menuItems, appProps, children } = props
  return (
    <Fragment>
      <Head>
        <title>{title}</title>
        {process.env.NODE_ENV !== 'production' && (
          <link rel="stylesheet" type="text/css" href={'/_next/static/css/styles.chunk.css?v=' + Date.now()}/>
        )}
      </Head>
      <TopMenu {...appProps}>
        {menuItems}
      </TopMenu>
      <div style={{
        position: 'absolute',
        top: '4rem',
        padding: 0,
        margin: 0,
        width: '100%',
        display: 'flex',
        flexFlow: 'column',
      }}>
        {children}
      </div>
    </Fragment>
  )
}

export default appLayout
