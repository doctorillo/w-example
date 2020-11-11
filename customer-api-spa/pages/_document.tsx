import React from 'react'
import Document, { Head, Html, Main, NextScript } from 'next/document'
import theme from '../components/theme'

class MyDocument extends Document {
  render () {
    return (
      <Html>
        <Head>
          <meta charSet="utf-8"/>
          <meta name="theme-color" content={theme.palette.primary.main}/>
          <meta name='viewport' content='initial-scale=1,maximum-scale=1,width=device-width,user-scalable=no,shrink-to-fit=no'/>
          <link href="/css/date-picker.css" rel="stylesheet"/>
          <link href="/css/mapbox-gl.css" rel="stylesheet"/>
          <link href="/css/ptrootui.css" rel="stylesheet"/>
          <link href="https://fonts.googleapis.com/css?family=Roboto:300,400,500&display=swap"
                rel="stylesheet"
          />
          <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
          <style dangerouslySetInnerHTML={{
            __html: `
              html {
                  font-family: 'PT Root UI';
                  font-size: 100%;
                  width: 100%;
                  height: 100%;
                }
              body { margin: 0; padding: 0; width: 100%; height: 100%; box-sizing: border-box; background-color: #f2f5fb; }
              #__next { display: flex; flex-flow: column; width: 100%; height: 100%; }
            `,
          }}/>
        </Head>
        <body>
        <Main/>
        <NextScript/>
        </body>
      </Html>
    )
  }
}

export default MyDocument