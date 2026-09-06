import { Box, Typography } from '@mui/material';

export default function PageTitle({ title, subtitle, action }) {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 2, mb: 3 }}>
      <Box><Typography variant="h4">{title}</Typography>{subtitle && <Typography color="text.secondary" sx={{ mt: .5 }}>{subtitle}</Typography>}</Box>
      {action}
    </Box>
  );
}
