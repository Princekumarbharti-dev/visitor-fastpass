import { useEffect, useState } from 'react';
import { Button, Card, Dialog, DialogContent, DialogTitle, MenuItem, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { toast } from 'react-toastify';
import PageTitle from '../components/PageTitle';
import StatusChip from '../components/StatusChip';
import api, { apiMessage } from '../api/client';

const statuses = ['', 'PENDING', 'APPROVED', 'REJECTED', 'CHECKED_IN', 'COMPLETED', 'CANCELLED', 'EXPIRED'];

export default function AdminVisitsPage() {
  const [visits, setVisits] = useState([]); const [status, setStatus] = useState(''); const [search, setSearch] = useState(''); const [detail, setDetail] = useState(null);
  const load = async () => { try { const { data } = await api.get('/api/v1/admin/visits', { params: { status: status || undefined, search: search || undefined, page: 0, size: 100 } }); setVisits(data.content); } catch (e) { toast.error(apiMessage(e, 'Unable to load visitor records')); } };
  useEffect(() => { load(); }, [status]);
  const showDetail = async (id) => { try { setDetail((await api.get(`/api/v1/admin/visits/${id}`)).data); } catch (e) { toast.error(apiMessage(e)); } };
  return <><PageTitle title="Visitor records" subtitle="Search the complete visitor lifecycle and audit history." /><Card sx={{ mb: 2, p: 2 }}><Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}><TextField fullWidth label="Search name, email, mobile or reference" value={search} onChange={(e) => setSearch(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && load()} /><TextField select label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ minWidth: 180 }}>{statuses.map((item) => <MenuItem key={item || 'ALL'} value={item}>{item || 'All statuses'}</MenuItem>)}</TextField><Button variant="contained" onClick={load}>Search</Button></Stack></Card>
    <TableContainer component={Card}><Table><TableHead><TableRow><TableCell>Visitor</TableCell><TableCell>Host</TableCell><TableCell>Schedule</TableCell><TableCell>Status</TableCell><TableCell>Pass</TableCell><TableCell /></TableRow></TableHead><TableBody>{visits.map((v) => <TableRow key={v.id} hover><TableCell><Typography fontWeight={700}>{v.visitorName}</Typography><Typography variant="caption">{v.publicReference}</Typography></TableCell><TableCell>{v.hostName}</TableCell><TableCell>{new Date(v.scheduledAt).toLocaleString()}</TableCell><TableCell><StatusChip value={v.status} /></TableCell><TableCell><StatusChip value={v.passStatus} /></TableCell><TableCell><Button onClick={() => showDetail(v.id)}>Details</Button></TableCell></TableRow>)}</TableBody></Table></TableContainer>
    <Dialog open={Boolean(detail)} onClose={() => setDetail(null)} fullWidth maxWidth="md"><DialogTitle>Visit timeline</DialogTitle><DialogContent>{detail && <><Typography variant="h6">{detail.visit.visitorName} · {detail.visit.publicReference}</Typography><Typography color="text.secondary">{detail.visit.purpose}</Typography><Stack spacing={1.5} sx={{ mt: 3 }}>{detail.events.map((event, index) => <Card key={`${event.eventType}-${index}`} variant="outlined" sx={{ p: 2 }}><Stack direction="row" justifyContent="space-between"><StatusChip value={event.eventType} /><Typography variant="caption">{new Date(event.eventTime).toLocaleString()}</Typography></Stack><Typography variant="body2" sx={{ mt: 1 }}>{event.performedByName}{event.remarks ? ` · ${event.remarks}` : ''}</Typography></Card>)}</Stack></>}</DialogContent></Dialog>
  </>;
}
