import React, { useState } from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  AppBar, Avatar, Box, Button, Container, Divider, Drawer, IconButton, List,
  ListItemButton, ListItemIcon, ListItemText, Stack, Toolbar, Typography,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import BadgeOutlinedIcon from '@mui/icons-material/BadgeOutlined';
import DashboardOutlinedIcon from '@mui/icons-material/DashboardOutlined';
import FactCheckOutlinedIcon from '@mui/icons-material/FactCheckOutlined';
import QrCodeScannerOutlinedIcon from '@mui/icons-material/QrCodeScannerOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import NotificationsNoneOutlinedIcon from '@mui/icons-material/NotificationsNoneOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import { useAuth } from '../auth/AuthContext';

const roleLinks = {
  ADMIN: [
    ['Dashboard', '/admin', <DashboardOutlinedIcon />],
    ['Visitor records', '/admin/visits', <FactCheckOutlinedIcon />],
    ['Employees', '/admin/employees', <PeopleAltOutlinedIcon />],
    ['Notifications', '/admin/notifications', <NotificationsNoneOutlinedIcon />],
  ],
  HOST: [['Visit requests', '/host', <FactCheckOutlinedIcon />]],
  RECEPTION: [['Gate desk', '/reception', <QrCodeScannerOutlinedIcon />]],
};

export default function AppLayout() {
  const [open, setOpen] = useState(false);
  const { session, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const links = roleLinks[session?.role] || [];

  const navigation = (
    <Box sx={{ width: 270, py: 2 }} role="presentation" onClick={() => setOpen(false)}>
      <Stack direction="row" alignItems="center" spacing={1.5} sx={{ px: 2.5, pb: 2 }}>
        <Avatar sx={{ bgcolor: 'primary.main' }}><BadgeOutlinedIcon /></Avatar>
        <Box><Typography fontWeight={800}>Visitor FastPass</Typography><Typography variant="caption" color="text.secondary">Secure entry, made simple</Typography></Box>
      </Stack>
      <Divider />
      <List sx={{ px: 1.5 }}>
        {links.map(([label, path, icon]) => (
          <ListItemButton key={path} component={Link} to={path} selected={location.pathname === path}
            sx={{ my: .5, borderRadius: 2 }}>
            <ListItemIcon sx={{ minWidth: 40 }}>{icon}</ListItemIcon>
            <ListItemText primary={label} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ minHeight: '100vh' }}>
      <AppBar position="sticky" color="inherit" elevation={0} sx={{ borderBottom: 1, borderColor: 'divider' }}>
        <Toolbar>
          <IconButton onClick={() => setOpen(true)} sx={{ display: { md: 'none' }, mr: 1 }}><MenuIcon /></IconButton>
          <Stack direction="row" alignItems="center" spacing={1} sx={{ flexGrow: 1 }}>
            <BadgeOutlinedIcon color="primary" />
            <Typography variant="h6">Visitor FastPass</Typography>
          </Stack>
          <Box sx={{ textAlign: 'right', mr: 2, display: { xs: 'none', sm: 'block' } }}>
            <Typography variant="body2" fontWeight={700}>{session?.displayName}</Typography>
            <Typography variant="caption" color="text.secondary">{session?.role}</Typography>
          </Box>
          <Button startIcon={<LogoutOutlinedIcon />} onClick={() => { logout(); navigate('/'); }}>Logout</Button>
        </Toolbar>
      </AppBar>
      <Drawer open={open} onClose={() => setOpen(false)}>{navigation}</Drawer>
      <Box sx={{ display: 'flex' }}>
        <Box sx={{ display: { xs: 'none', md: 'block' }, borderRight: 1, borderColor: 'divider', minHeight: 'calc(100vh - 65px)', bgcolor: 'background.paper' }}>{navigation}</Box>
        <Container maxWidth="xl" sx={{ py: { xs: 3, md: 4 }, flex: 1 }}><Outlet /></Container>
      </Box>
    </Box>
  );
}
