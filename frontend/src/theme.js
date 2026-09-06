import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#0b5d8f', dark: '#073b5c', light: '#4d8db3' },
    secondary: { main: '#e08a1e' },
    background: { default: '#f3f7fa', paper: '#ffffff' },
    success: { main: '#16845b' },
  },
  typography: {
    fontFamily: 'Inter, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    h4: { fontWeight: 750 },
    h5: { fontWeight: 700 },
    h6: { fontWeight: 700 },
    button: { textTransform: 'none', fontWeight: 700 },
  },
  shape: { borderRadius: 12 },
  components: {
    MuiCard: { styleOverrides: { root: { boxShadow: '0 8px 28px rgba(11,59,96,.08)' } } },
    MuiButton: { defaultProps: { disableElevation: true } },
  },
});

export default theme;
