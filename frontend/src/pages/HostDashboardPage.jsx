import { useEffect, useState } from 'react';
import { Box, Button, Card, CardContent, Dialog, DialogActions, DialogContent, DialogTitle, Grid2, Tab, Tabs, TextField, Typography } from '@mui/material';
import { toast } from 'react-toastify';
import PageTitle from '../components/PageTitle';
import StatusChip from '../components/StatusChip';
import api, { apiMessage } from '../api/client';

export default function HostDashboardPage() {
  const [tab, setTab] = useState(0); const [pending, setPending] = useState([]); const [history, setHistory] = useState([]);
  const [dialog, setDialog] = useState(null); const [comment, setComment] = useState('');
  const load = async () => { try { const [p, h] = await Promise.all([api.get('/api/v1/host/visits/pending'), api.get('/api/v1/host/visits/history')]); setPending(p.data); setHistory(h.data); } catch (e) { toast.error(apiMessage(e, 'Unable to load requests')); } };
  useEffect(() => { load(); }, []);
  const decide = async () => { try { const reject = dialog.action === 'reject'; await api.patch(`/api/v1/host/visits/${dialog.visit.id}/${reject ? 'reject' : 'approve'}`, reject ? { reason: comment } : { comment }); toast.success(reject ? 'Visit rejected' : 'Visit approved and pass requested'); setDialog(null); setComment(''); load(); } catch (e) { toast.error(apiMessage(e, 'Unable to update visit')); } };
  const visits = tab === 0 ? pending : history;
  return <><PageTitle title="Host visits" subtitle="Review requests assigned to you and track previous decisions." /><Card sx={{ mb: 3 }}><Tabs value={tab} onChange={(_, value) => setTab(value)}><Tab label={`Pending (${pending.length})`} /><Tab label="History" /></Tabs></Card>
    <Grid2 container spacing={2}>{visits.length === 0 && <Grid2 size={12}><Card><CardContent><Typography color="text.secondary">No visits in this view.</Typography></CardContent></Card></Grid2>}{visits.map((visit) => <Grid2 key={visit.id} size={{ xs: 12, lg: 6 }}><Card><CardContent><Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 2 }}><Box><Typography variant="h6">{visit.visitorName}</Typography><Typography color="text.secondary">{visit.organizationName} · {visit.visitorEmail}</Typography></Box><StatusChip value={visit.status} /></Box><Typography sx={{ mt: 2 }}><strong>Purpose:</strong> {visit.purpose}</Typography><Typography><strong>Scheduled:</strong> {new Date(visit.scheduledAt).toLocaleString()}</Typography>{visit.status === 'PENDING' && <Box sx={{ display: 'flex', gap: 1, mt: 2 }}><Button variant="contained" color="success" onClick={() => setDialog({ action: 'approve', visit })}>Approve</Button><Button variant="outlined" color="error" onClick={() => setDialog({ action: 'reject', visit })}>Reject</Button></Box>}</CardContent></Card></Grid2>)}</Grid2>
    <Dialog open={Boolean(dialog)} onClose={() => setDialog(null)} fullWidth maxWidth="sm"><DialogTitle>{dialog?.action === 'reject' ? 'Reject visit' : 'Approve visit'}</DialogTitle><DialogContent><Typography sx={{ mb: 2 }}>{dialog?.visit?.visitorName}</Typography><TextField autoFocus fullWidth multiline minRows={3} label={dialog?.action === 'reject' ? 'Rejection reason' : 'Approval comment (optional)'} value={comment} onChange={(e) => setComment(e.target.value)} /></DialogContent><DialogActions><Button onClick={() => setDialog(null)}>Cancel</Button><Button variant="contained" color={dialog?.action === 'reject' ? 'error' : 'success'} disabled={dialog?.action === 'reject' && comment.trim().length < 3} onClick={decide}>Confirm</Button></DialogActions></Dialog>
  </>;
}
