import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Alert, Button, Card, CardContent, Container, Divider, Stack, TextField, Typography } from '@mui/material';
import PublicHeader from '../components/PublicHeader';
import StatusChip from '../components/StatusChip';
import api, { apiMessage } from '../api/client';

export default function VisitStatusPage() {
  const [params, setParams] = useSearchParams();
  const [reference, setReference] = useState(params.get('reference') || '');
  const [visit, setVisit] = useState(null);
  const [error, setError] = useState('');
  const search = async () => { try { setError(''); const ref = reference.trim().toUpperCase(); const { data } = await api.get(`/api/v1/public/visits/status/${encodeURIComponent(ref)}`); setVisit(data); setParams({ reference: ref }); } catch (e) { setVisit(null); setError(apiMessage(e, 'Visit not found')); } };
  return <><PublicHeader /><Container maxWidth="sm" sx={{ py: 6 }}><Typography variant="h4">Check visit status</Typography><Typography color="text.secondary" sx={{ mb: 3 }}>Enter the reference provided after registration.</Typography><Stack direction="row" spacing={1}><TextField fullWidth label="Reference number" value={reference} onChange={(e) => setReference(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && search()} /><Button variant="contained" onClick={search}>Check</Button></Stack>{error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
    {visit && <Card sx={{ mt: 3 }}><CardContent sx={{ p: 3 }}><Stack direction="row" justifyContent="space-between"><Typography variant="h6">{visit.visitorName}</Typography><StatusChip value={visit.status} /></Stack><Typography color="text.secondary">{visit.publicReference}</Typography><Divider sx={{ my: 2 }} /><Typography><strong>Host:</strong> {visit.hostName}</Typography><Typography><strong>Scheduled:</strong> {new Date(visit.scheduledAt).toLocaleString()}</Typography><Typography component="div" sx={{ mt: 1 }}><strong>Pass:</strong> <StatusChip value={visit.passStatus} /></Typography>{visit.decisionComment && <Alert severity={visit.status === 'REJECTED' ? 'error' : 'info'} sx={{ mt: 2 }}>{visit.decisionComment}</Alert>}{visit.status === 'APPROVED' && visit.passNumber && <Button component={Link} to={`/pass/${visit.passNumber}`} variant="outlined" sx={{ mt: 2 }}>Open digital pass</Button>}</CardContent></Card>}
  </Container></>;
}
