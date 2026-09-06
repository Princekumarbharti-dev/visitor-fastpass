import { Link } from 'react-router-dom';
import { Box, Button, Card, CardContent, Container, Grid2, Stack, Typography } from '@mui/material';
import HowToRegOutlinedIcon from '@mui/icons-material/HowToRegOutlined';
import VerifiedUserOutlinedIcon from '@mui/icons-material/VerifiedUserOutlined';
import QrCode2OutlinedIcon from '@mui/icons-material/QrCode2Outlined';
import PublicHeader from '../components/PublicHeader';

export default function HomePage() {
  return <><PublicHeader />
    <Box className="hero-grid" sx={{ bgcolor: 'primary.dark', color: 'white', py: { xs: 8, md: 12 } }}>
      <Container maxWidth="lg"><Grid2 container spacing={6} alignItems="center">
        <Grid2 size={{ xs: 12, md: 7 }}>
          <Typography variant="overline" sx={{ color: 'secondary.light', letterSpacing: 2 }}>Welcome with confidence</Typography>
          <Typography variant="h2" fontWeight={850} sx={{ fontSize: { xs: '2.6rem', md: '4rem' }, mt: 1 }}>A faster, safer way to welcome every visitor.</Typography>
          <Typography variant="h6" sx={{ opacity: .8, fontWeight: 400, mt: 2, maxWidth: 650 }}>Register ahead, receive host approval, and enter with a secure digital FastPass.</Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 4 }}>
            <Button component={Link} to="/register" variant="contained" color="secondary" size="large">Register a visit</Button>
            <Button component={Link} to="/status" variant="outlined" size="large" sx={{ color: 'white', borderColor: 'rgba(255,255,255,.5)' }}>Check request status</Button>
          </Stack>
        </Grid2>
        <Grid2 size={{ xs: 12, md: 5 }}><Card sx={{ bgcolor: 'rgba(255,255,255,.1)', color: 'white', backdropFilter: 'blur(10px)' }}><CardContent sx={{ p: 4 }}><QrCode2OutlinedIcon sx={{ fontSize: 150, display: 'block', mx: 'auto' }} /><Typography align="center" variant="h5">One secure QR pass</Typography><Typography align="center" sx={{ opacity: .75, mt: 1 }}>From approval to entry and exit.</Typography></CardContent></Card></Grid2>
      </Grid2></Container>
    </Box>
    <Container maxWidth="lg" sx={{ py: 7 }}><Grid2 container spacing={3}>{[
      [<HowToRegOutlinedIcon />, 'Register in minutes', 'Share visit details and choose the employee you are meeting.'],
      [<VerifiedUserOutlinedIcon />, 'Host-approved access', 'Every request is reviewed by the intended host before entry.'],
      [<QrCode2OutlinedIcon />, 'Contactless FastPass', 'Present your encrypted QR credential at reception for verification.'],
    ].map(([icon, title, body]) => <Grid2 key={title} size={{ xs: 12, md: 4 }}><Card sx={{ height: '100%' }}><CardContent sx={{ p: 3 }}>{icon}<Typography variant="h6" sx={{ my: 1 }}>{title}</Typography><Typography color="text.secondary">{body}</Typography></CardContent></Card></Grid2>)}</Grid2></Container>
  </>;
}
