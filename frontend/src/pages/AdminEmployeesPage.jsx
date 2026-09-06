import { useEffect, useState } from 'react';
import { Button, Card, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Stack, Switch, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { Form, Formik } from 'formik';
import * as Yup from 'yup';
import { toast } from 'react-toastify';
import PageTitle from '../components/PageTitle';
import api, { apiMessage } from '../api/client';

const initial = { username: '', email: '', password: '', role: 'HOST', employeeCode: '', fullName: '', department: '', designation: '', phone: '' };
const schema = Yup.object({ username: Yup.string().required(), email: Yup.string().email().required(), password: Yup.string().min(8).required(), role: Yup.string().required(), employeeCode: Yup.string().required(), fullName: Yup.string().required() });

export default function AdminEmployeesPage() {
  const [employees, setEmployees] = useState([]); const [open, setOpen] = useState(false);
  const load = () => api.get('/api/v1/admin/employees').then(({ data }) => setEmployees(data)).catch((e) => toast.error(apiMessage(e)));
  useEffect(() => { load(); }, []);
  const toggle = async (employee) => { try { await api.patch(`/api/v1/admin/employees/${employee.id}/status`, { active: !employee.active }); toast.success('Employee status updated'); load(); } catch (e) { toast.error(apiMessage(e)); } };
  return <><PageTitle title="Employees" subtitle="Manage hosts, reception users, and administrators." action={<Button variant="contained" onClick={() => setOpen(true)}>Add employee</Button>} /><TableContainer component={Card}><Table><TableHead><TableRow><TableCell>Employee</TableCell><TableCell>Role</TableCell><TableCell>Department</TableCell><TableCell>Designation</TableCell><TableCell>Active</TableCell></TableRow></TableHead><TableBody>{employees.map((e) => <TableRow key={e.id}><TableCell><Typography fontWeight={700}>{e.fullName}</Typography><Typography variant="caption">{e.email} · {e.employeeCode}</Typography></TableCell><TableCell>{e.role}</TableCell><TableCell>{e.department || '—'}</TableCell><TableCell>{e.designation || '—'}</TableCell><TableCell><Switch checked={e.active} onChange={() => toggle(e)} /></TableCell></TableRow>)}</TableBody></Table></TableContainer>
    <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm"><DialogTitle>Add employee</DialogTitle><Formik initialValues={initial} validationSchema={schema} onSubmit={async (values, actions) => { try { await api.post('/api/v1/admin/employees', values); toast.success('Employee created'); setOpen(false); load(); } catch (e) { toast.error(apiMessage(e)); } finally { actions.setSubmitting(false); } }}>{({ values, handleChange, errors, touched, isSubmitting }) => <Form><DialogContent><Stack spacing={2}>{Object.keys(initial).map((name) => name === 'role' ? <TextField key={name} select name={name} label="Role" value={values[name]} onChange={handleChange}><MenuItem value="HOST">Host</MenuItem><MenuItem value="RECEPTION">Reception</MenuItem><MenuItem value="ADMIN">Admin</MenuItem></TextField> : <TextField key={name} name={name} type={name === 'password' ? 'password' : 'text'} label={name.replace(/([A-Z])/g, ' $1').replace(/^./, (c) => c.toUpperCase())} value={values[name]} onChange={handleChange} error={touched[name] && Boolean(errors[name])} helperText={touched[name] && errors[name]} />)}</Stack></DialogContent><DialogActions><Button onClick={() => setOpen(false)}>Cancel</Button><Button type="submit" variant="contained" disabled={isSubmitting}>Create</Button></DialogActions></Form>}</Formik></Dialog>
  </>;
}
