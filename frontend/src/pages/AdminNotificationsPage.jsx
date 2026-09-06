import { useEffect, useState } from 'react';
import { Button, Card, Link, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import { toast } from 'react-toastify';
import PageTitle from '../components/PageTitle';
import StatusChip from '../components/StatusChip';
import api, { apiMessage } from '../api/client';

export default function AdminNotificationsPage() {
  const [items, setItems] = useState([]);
  const load = () => api.get('/api/v1/admin/notifications', { params: { page: 0, size: 100 } }).then(({ data }) => setItems(data.content)).catch((e) => toast.error(apiMessage(e)));
  useEffect(() => { load(); }, []);
  const retry = async (id) => { try { await api.post(`/api/v1/admin/notifications/${id}/retry`); toast.success('Notification processed'); load(); } catch (e) { toast.error(apiMessage(e)); } };
  return <><PageTitle title="Notifications" subtitle="Review QR pass email delivery and retry failed messages." /><TableContainer component={Card}><Table><TableHead><TableRow><TableCell>Recipient</TableCell><TableCell>Pass</TableCell><TableCell>Status</TableCell><TableCell>Created</TableCell><TableCell>Digital pass</TableCell><TableCell /></TableRow></TableHead><TableBody>{items.map((item) => <TableRow key={item.id}><TableCell><Typography fontWeight={700}>{item.recipientName}</Typography><Typography variant="caption">{item.recipientEmail}</Typography></TableCell><TableCell>{item.passNumber}</TableCell><TableCell><StatusChip value={item.status} /></TableCell><TableCell>{new Date(item.createdAt).toLocaleString()}</TableCell><TableCell>{item.digitalPassUrl ? <Link href={item.digitalPassUrl} target="_blank" rel="noreferrer">Open pass</Link> : '—'}</TableCell><TableCell><Button onClick={() => retry(item.id)}>Retry</Button></TableCell></TableRow>)}</TableBody></Table></TableContainer></>;
}
