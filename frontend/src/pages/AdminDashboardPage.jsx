import { useEffect, useState } from 'react';
import { Card, CardContent, Grid2, Typography } from '@mui/material';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import TodayOutlinedIcon from '@mui/icons-material/TodayOutlined';
import HourglassTopOutlinedIcon from '@mui/icons-material/HourglassTopOutlined';
import LoginOutlinedIcon from '@mui/icons-material/LoginOutlined';
import TaskAltOutlinedIcon from '@mui/icons-material/TaskAltOutlined';
import { toast } from 'react-toastify';
import PageTitle from '../components/PageTitle';
import api, { apiMessage } from '../api/client';

const cards = [
  ['Total visitors', 'totalVisitors', <PeopleAltOutlinedIcon />], ['Visitors today', 'visitorsToday', <TodayOutlinedIcon />],
  ['Pending approvals', 'pendingApprovals', <HourglassTopOutlinedIcon />], ['Currently inside', 'checkedInVisitors', <LoginOutlinedIcon />],
  ['Completed visits', 'completedVisits', <TaskAltOutlinedIcon />],
];

export default function AdminDashboardPage() {
  const [stats, setStats] = useState({});
  useEffect(() => { api.get('/api/v1/admin/dashboard').then(({ data }) => setStats(data)).catch((e) => toast.error(apiMessage(e, 'Unable to load dashboard'))); }, []);
  return <><PageTitle title="Operations dashboard" subtitle="A live overview of visitor activity and gate status." /><Grid2 container spacing={2}>{cards.map(([title,key,icon]) => <Grid2 key={key} size={{ xs: 12, sm: 6, lg: 2.4 }}><Card sx={{ height: '100%' }}><CardContent><Typography color="primary" sx={{ mb: 2 }}>{icon}</Typography><Typography variant="h4">{stats[key] ?? '—'}</Typography><Typography color="text.secondary">{title}</Typography></CardContent></Card></Grid2>)}</Grid2></>;
}
