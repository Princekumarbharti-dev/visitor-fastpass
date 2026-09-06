import { Chip } from '@mui/material';

const colors = {
  PENDING: 'warning', APPROVED: 'info', REJECTED: 'error', CHECKED_IN: 'success',
  COMPLETED: 'default', ACTIVE: 'success', GENERATED: 'success', FAILED: 'error',
  SENT: 'success', SIMULATED: 'info', REVOKED: 'error', USED: 'default',
};

export default function StatusChip({ value }) {
  return <Chip label={(value || 'UNKNOWN').replaceAll('_', ' ')} size="small" color={colors[value] || 'default'} />;
}
