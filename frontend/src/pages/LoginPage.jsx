import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { Alert, Box, Button, Card, CardContent, Container, TextField, Typography } from '@mui/material';
import { Form, Formik } from 'formik';
import * as Yup from 'yup';
import { toast } from 'react-toastify';
import PublicHeader from '../components/PublicHeader';
import { useAuth } from '../auth/AuthContext';
import { apiMessage } from '../api/client';

const schema = Yup.object({ username: Yup.string().required('Username is required'), password: Yup.string().required('Password is required') });
const landing = { ADMIN: '/admin', HOST: '/host', RECEPTION: '/reception' };

export default function LoginPage() {
  const { login, session } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState('');
  if (session) return <Navigate to={landing[session.role] || '/'} replace />;
  return <><PublicHeader /><Container maxWidth="sm" sx={{ py: 7 }}><Card><CardContent sx={{ p: { xs: 3, md: 5 } }}>
    <Typography variant="h4">Staff sign in</Typography><Typography color="text.secondary" sx={{ mb: 3 }}>For administrators, hosts, and reception staff.</Typography>
    {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
    <Formik initialValues={{ username: '', password: '' }} validationSchema={schema} onSubmit={async (values, actions) => {
      try { const data = await login(values); toast.success(`Welcome, ${data.displayName}`); navigate(location.state?.from?.pathname || landing[data.role] || '/'); }
      catch (err) { setError(apiMessage(err, 'Login failed')); } finally { actions.setSubmitting(false); }
    }}>{({ values, handleChange, touched, errors, isSubmitting }) => <Form><TextField fullWidth margin="normal" name="username" label="Username" value={values.username} onChange={handleChange} error={touched.username && Boolean(errors.username)} helperText={touched.username && errors.username} /><TextField fullWidth margin="normal" type="password" name="password" label="Password" value={values.password} onChange={handleChange} error={touched.password && Boolean(errors.password)} helperText={touched.password && errors.password} /><Button fullWidth type="submit" variant="contained" size="large" disabled={isSubmitting} sx={{ mt: 3 }}>{isSubmitting ? 'Signing in…' : 'Sign in'}</Button></Form>}</Formik>
    <Box sx={{ mt: 3, p: 2, bgcolor: 'background.default', borderRadius: 2 }}><Typography variant="caption">Demo users: admin / Admin@123 · asha.host / Host@123 · reception / Reception@123</Typography></Box>
  </CardContent></Card></Container></>;
}
