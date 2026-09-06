import { useEffect, useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { Alert, Box, Card, CardContent, CircularProgress, Container, Divider, Stack, TextField, Typography } from '@mui/material';
import PublicHeader from '../components/PublicHeader';
import StatusChip from '../components/StatusChip';
import api, { API_BASE_URL, apiMessage } from '../api/client';

export default function DigitalPassPage() {
  const { passNumber } = useParams();
  const [params] = useSearchParams();
  const [token, setToken] = useState(params.get('token') || '');
  const [pass, setPass] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => { if (!token) return; api.get(`/api/v1/public/passes/${passNumber}`, { params: { token } }).then(({ data }) => setPass(data)).catch((e) => setError(apiMessage(e, 'Unable to open pass'))); }, [passNumber, token]);
  return <><PublicHeader /><Container maxWidth="sm" sx={{ py: 5 }}>{!token && <Alert severity="info" sx={{ mb: 2 }}>Paste the secure token from your approval email.</Alert>}<TextField fullWidth label="Secure pass token" value={token} onChange={(e) => setToken(e.target.value)} sx={{ mb: 2 }} />{token && !pass && !error && <Box textAlign="center"><CircularProgress /></Box>}{error && <Alert severity="error">{error}</Alert>}{pass && <Card><CardContent sx={{ p: 4, textAlign: 'center' }}><Typography variant="overline">Visitor FastPass</Typography><Typography variant="h4">{pass.visitorName}</Typography><Typography color="text.secondary">{pass.passNumber}</Typography><Box component="img" alt="Visitor QR pass" src={`${API_BASE_URL}/api/v1/public/passes/${encodeURIComponent(passNumber)}/qr?token=${encodeURIComponent(token)}`} sx={{ width: 280, maxWidth: '100%', my: 2 }} /><Stack direction="row" justifyContent="center"><StatusChip value={pass.status} /></Stack><Divider sx={{ my: 2 }} /><Typography><strong>Host:</strong> {pass.hostName}</Typography><Typography><strong>Purpose:</strong> {pass.purpose}</Typography><Typography><strong>Visit:</strong> {new Date(pass.scheduledAt).toLocaleString()}</Typography><Typography variant="caption" color="text.secondary" display="block" sx={{ mt: 2 }}>Valid until {new Date(pass.validUntil).toLocaleString()}</Typography></CardContent></Card>}</Container></>;
}
