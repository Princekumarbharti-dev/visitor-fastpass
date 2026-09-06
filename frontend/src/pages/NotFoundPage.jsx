import { Link } from 'react-router-dom';
import { Button, Container, Typography } from '@mui/material';

export default function NotFoundPage() {
  return <Container maxWidth="sm" sx={{ py: 10, textAlign: 'center' }}><Typography variant="h2">404</Typography><Typography variant="h5">This page could not be found.</Typography><Button component={Link} to="/" variant="contained" sx={{ mt: 3 }}>Go home</Button></Container>;
}
