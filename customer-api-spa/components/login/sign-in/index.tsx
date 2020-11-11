import React, { useState } from 'react'
import { AppProps } from '../../../redux/modules/env/connect'
import useStyles from './styles'
import TextField from '@material-ui/core/TextField'
import Button from '@material-ui/core/Button'
import FormLabel from '@material-ui/core/FormLabel'
import FormControl from '@material-ui/core/FormControl'

const signInPopup: React.FC<AppProps> = props => {
  const style = useStyles()
  const { signIn } = props.appFn
  const [userName, setUserName] = useState('')
  const [userPassword, setUserPassword] = useState('')
  return <div className={style.root}>
    <FormControl component="fieldset" fullWidth={true}>
      <FormLabel>Имя пользователя</FormLabel>
      <TextField value={userName} onChange={(e) => setUserName(e.target.value)}/>
      <FormLabel>Пароль</FormLabel>
      <TextField type={'password'} value={userPassword} onChange={(e) => setUserPassword(e.target.value)}/>
      <Button href={'#'} onClick={() => signIn({
        name: userName,
        password: userPassword,
      })}>
        Check
      </Button>
    </FormControl>
  </div>
}

export default signInPopup