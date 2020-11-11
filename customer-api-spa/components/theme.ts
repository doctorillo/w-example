import { createMuiTheme, responsiveFontSizes } from '@material-ui/core/styles';
import red from '@material-ui/core/colors/red';

const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#159363',
    },
    secondary: {
      main: '#3970be',
    },
    error: {
      main: red.A400,
    },
    background: {
      default: '#f2f5fb',
    },
  },
});
const rt = responsiveFontSizes(theme);
const cssEnv = {
  palette: {
    pageBackground: '#f2f5fb',
    pageBackgroundDarker: '#ebeef2',
    accent: '#fce157',
    primary: '#159363',
    primaryLight: '#21a349',
    primaryDark: '#388a6b',
    secondary: '#3970be',
    secondaryLight: '#418edf',
    text: '#16181b',
    textLight: '#252144',
    menuText: '#16181b',
    menuTextLight: '#b9c0cd',
    menuTextExtraLight: '#d7deec',
    subMenu: '#3a3c40',
  },
  typo: {
    family: 'PT Root UI',
    familySecondary: 'Roboto, sans-serif',
    size: '1rem',
    weightLight: 300,
    weightRegular: 400,
    weightMedium: 700,
  },
  menu: {
    heightSmall: '5.3125rem',
    heightHd: '5rem',
  },
  propertyCard: {
    widthHd: '20rem',
    heightHd: '11rem',
    photo: {
      widthHd: '16rem',
      heightHd: '10rem',
    },
    price: {
      widthHd: '14rem',
      heightHd: '10rem',
    }
  },
  excursionCard: {
    widthHd: '20rem',
    heightHd: '15rem',
    photo: {
      widthHd: '16rem',
      heightHd: '15rem',
    },
    price: {
      widthHd: '10rem',
      heightHd: '15rem',
    }
  },
}
const appTheme = {...rt, cssEnv}
export type AppTheme = typeof appTheme;
export default appTheme;