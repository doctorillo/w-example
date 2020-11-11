import React, { useState, useEffect } from 'react'
import Router from 'next/router'
import { makeStyles } from '@material-ui/core/styles'
import Paper from '@material-ui/core/Paper'
import FormControl from '@material-ui/core/FormControl'
import Input from '@material-ui/core/Input'
import FormHelperText from '@material-ui/core/FormHelperText'
import InputAdornment from '@material-ui/core/InputAdornment'
import Account from '@material-ui/icons/AccountCircleOutlined'
import Visibility from '@material-ui/icons/Visibility'
import VisibilityOff from '@material-ui/icons/VisibilityOff'
import Button from '@material-ui/core/Button'
import { AppProps } from '../../redux/modules/env/connect'
import { ResultKind } from '../../types/ResultKind'
import { Nullable } from '../../types/Nullable'

const useStyles = makeStyles(theme => ({
  paper: {
    display: 'flex',
    flexFlow: 'column nowrap',
    padding: theme.spacing(2),
    alignSelf: 'center',
    justifyContent: 'center',
    width: '23rem',
    height: '23rem',
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
  formControl: {
    margin: theme.spacing(2),
  },
}))

const login  = (props: AppProps) => {
  const { status } = props.appEnv
  const { signIn } = props.appFn
  const style = useStyles()
  const [view, setView] = useState<boolean>(false)
  const [name, setName] = useState<Nullable<string>>(null)
  const [password, setPassword] = useState<Nullable<string>>(null)
  const inputType = view ? 'text' : 'password'
  useEffect(() => {
    if (status === ResultKind.Completed) {
      Router.push('/')
    }
  }, [status])
  return (<Paper className={style.paper}>
    <FormControl component="div" className={style.formControl}>
      <Input
        id="user-name-input"
        required={true}
        autoFocus={true}
        fullWidth={true}
        aria-describedby="user-name-helper-text"
        endAdornment={<InputAdornment position={'end'}>
          <Account color={'primary'}/>
        </InputAdornment>}
        value={name}
        onChange={({ target: { value } }) => setName(value)}
      />
      <FormHelperText id="user-name-helper-text" error={!name}>
        Адрес электронной почты
      </FormHelperText>
    </FormControl>
    <FormControl component="div" className={style.formControl}>
      <Input
        id="user-password-input"
        required={true}
        type={inputType}
        fullWidth={true}
        aria-describedby="user-password-helper-text"
        endAdornment={<InputAdornment position="end">
          {view ? <Visibility
            color={'primary'}
            onClick={() => setView(!view)}
          /> : <VisibilityOff
            onClick={() => setView(!view)}
            color={'primary'}
          />
          }
        </InputAdornment>}
        value={password}
        onChange={({ target: { value } }) => setPassword(value)}
      />
      <FormHelperText id="user-password-helper-text">
        Пароль
      </FormHelperText>
    </FormControl>
    <FormControl component="div" className={style.formControl}>
      <Button
        disabled={!name || !password}
        variant="contained"
        color="primary"
        onClick={() => name && password && signIn({
          name,
          password,
        })}
      >
        Войти
      </Button>
    </FormControl>
  </Paper>)
}

export default login