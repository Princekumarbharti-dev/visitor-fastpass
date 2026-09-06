import { Link } from 'react-router-dom';
import { AppBar, Button, Stack, Toolbar, Typography } from '@mui/material';
import BadgeOutlinedIcon from '@mui/icons-material/BadgeOutlined';

export default function PublicHeader() {
  return (
    <AppBar position="static" color="transparent" elevation={0} sx={{ color: 'inherit' }}>
      <Toolbar sx={{ maxWidth: 1200, width: '100%', mx: 'auto' }}>
        <BadgeOutlinedIcon color="primary" sx={{ mr: 1 }} />
        <Typography variant="h6" fontWeight={800} sx={{ flexGrow: 1 }}>Visitor FastPass</Typography>
        <Stack direction="row" spacing={1}>
          <Button component={Link} to="/status">Check status</Button>
          <Button component={Link} to="/login" variant="outlined">Staff login</Button>
        </Stack>
      </Toolbar>
    </AppBar>
  );
}
