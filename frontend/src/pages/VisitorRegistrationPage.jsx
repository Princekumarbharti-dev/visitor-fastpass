import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Alert, Button, Card, CardContent, Container, Grid2, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { Form, Formik } from 'formik';
import * as Yup from 'yup';
import { toast } from 'react-toastify';
import PublicHeader from '../components/PublicHeader';
import api, { apiMessage } from '../api/client';

const schema = Yup.object({
  fullName: Yup.string().min(2).max(100).required('Full name is required'),
  mobileNumber: Yup.string().matches(/^[0-9+() -]{7,20}$/, 'Enter a valid mobile number').required(),
  email: Yup.string().email().required(), organizationName: Yup.string().max(120).required(),
  purpose: Yup.string().min(3).max(300).required(), hostId: Yup.number().positive().required('Select a host'),
  scheduledAt: Yup.date().min(new Date(), 'Visit time must be in the future').required(),
});

export default function VisitorRegistrationPage() {
  const [hosts, setHosts] = useState([]);
  const [result, setResult] = useState(null);
  useEffect(() => { api.get('/api/v1/public/hosts').then(({ data }) => setHosts(data)).catch((e) => toast.error(apiMessage(e, 'Unable to load hosts'))); }, []);

  return <><PublicHeader /><Container maxWidth="md" sx={{ py: 5 }}>
    <Typography variant="h4">Register your visit</Typography><Typography color="text.secondary" sx={{ mb: 3 }}>Submit your details for host approval.</Typography>
    {result ? <Card><CardContent sx={{ p: 4 }}><Alert severity="success" sx={{ mb: 3 }}>Your visit request has been submitted.</Alert><Typography variant="overline">Reference number</Typography><Typography variant="h3" color="primary" sx={{ my: 1 }}>{result.publicReference}</Typography><Typography>Host: {result.hostName}</Typography><Typography>Scheduled: {new Date(result.scheduledAt).toLocaleString()}</Typography><Stack direction="row" spacing={2} sx={{ mt: 3 }}><Button component={Link} to={`/status?reference=${result.publicReference}`} variant="contained">Track status</Button><Button onClick={() => setResult(null)}>Register another</Button></Stack></CardContent></Card>
      : <Card><CardContent sx={{ p: { xs: 2.5, md: 4 } }}><Formik initialValues={{ fullName: '', mobileNumber: '', email: '', organizationName: '', purpose: '', hostId: '', scheduledAt: '' }} validationSchema={schema} onSubmit={async (values, actions) => {
        try { const payload = { ...values, hostId: Number(values.hostId), scheduledAt: new Date(values.scheduledAt).toISOString() }; const { data } = await api.post('/api/v1/public/visits', payload); setResult(data); toast.success('Visit request submitted'); }
        catch (e) { toast.error(apiMessage(e, 'Registration failed')); } finally { actions.setSubmitting(false); }
      }}>{({ values, handleChange, touched, errors, isSubmitting }) => <Form><Grid2 container spacing={2}>
        {[['fullName','Full name'],['mobileNumber','Mobile number'],['email','Email address'],['organizationName','Organization name']].map(([name,label]) => <Grid2 key={name} size={{ xs: 12, sm: 6 }}><TextField fullWidth name={name} label={label} value={values[name]} onChange={handleChange} error={touched[name] && Boolean(errors[name])} helperText={touched[name] && errors[name]} /></Grid2>)}
        <Grid2 size={{ xs: 12, sm: 6 }}><TextField select fullWidth name="hostId" label="Person to visit" value={values.hostId} onChange={handleChange} error={touched.hostId && Boolean(errors.hostId)} helperText={touched.hostId && errors.hostId}>{hosts.map((host) => <MenuItem key={host.id} value={host.id}>{host.fullName} · {host.department}</MenuItem>)}</TextField></Grid2>
        <Grid2 size={{ xs: 12, sm: 6 }}><TextField fullWidth type="datetime-local" name="scheduledAt" label="Visit date and time" value={values.scheduledAt} onChange={handleChange} slotProps={{ inputLabel: { shrink: true } }} error={touched.scheduledAt && Boolean(errors.scheduledAt)} helperText={touched.scheduledAt && errors.scheduledAt} /></Grid2>
        <Grid2 size={12}><TextField fullWidth multiline minRows={3} name="purpose" label="Purpose of visit" value={values.purpose} onChange={handleChange} error={touched.purpose && Boolean(errors.purpose)} helperText={touched.purpose && errors.purpose} /></Grid2>
        <Grid2 size={12}><Button type="submit" size="large" variant="contained" disabled={isSubmitting}>{isSubmitting ? 'Submitting…' : 'Submit visit request'}</Button></Grid2>
      </Grid2></Form>}</Formik></CardContent></Card>}
  </Container></>;
}
