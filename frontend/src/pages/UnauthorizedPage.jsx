import { Link } from 'react-router-dom';
import { Button, Container, Typography } from '@mui/material';

export default function UnauthorizedPage() {
  return <Container maxWidth="sm" sx={{ py: 10, textAlign: 'center' }}><Typography variant="h2">403</Typography><Typography variant="h5">You do not have access to this page.</Typography><Button component={Link} to="/" variant="contained" sx={{ mt: 3 }}>Go home</Button></Container>;
}
