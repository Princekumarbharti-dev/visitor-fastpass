import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './auth/ProtectedRoute';
import AppLayout from './components/AppLayout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import VisitorRegistrationPage from './pages/VisitorRegistrationPage';
import VisitStatusPage from './pages/VisitStatusPage';
import DigitalPassPage from './pages/DigitalPassPage';
import HostDashboardPage from './pages/HostDashboardPage';
import ReceptionPage from './pages/ReceptionPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import AdminVisitsPage from './pages/AdminVisitsPage';
import AdminEmployeesPage from './pages/AdminEmployeesPage';
import AdminNotificationsPage from './pages/AdminNotificationsPage';
import UnauthorizedPage from './pages/UnauthorizedPage';
import NotFoundPage from './pages/NotFoundPage';

const secured = (roles, element) => <ProtectedRoute roles={roles}>{element}</ProtectedRoute>;

export default function App() {
  return <Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<VisitorRegistrationPage />} />
    <Route path="/status" element={<VisitStatusPage />} />
    <Route path="/pass/:passNumber" element={<DigitalPassPage />} />
    <Route path="/unauthorized" element={<UnauthorizedPage />} />

    <Route element={secured(['ADMIN', 'HOST', 'RECEPTION'], <AppLayout />)}>
      <Route path="/host" element={secured(['HOST'], <HostDashboardPage />)} />
      <Route path="/reception" element={secured(['RECEPTION'], <ReceptionPage />)} />
      <Route path="/admin" element={secured(['ADMIN'], <AdminDashboardPage />)} />
      <Route path="/admin/visits" element={secured(['ADMIN'], <AdminVisitsPage />)} />
      <Route path="/admin/employees" element={secured(['ADMIN'], <AdminEmployeesPage />)} />
      <Route path="/admin/notifications" element={secured(['ADMIN'], <AdminNotificationsPage />)} />
    </Route>

    <Route path="*" element={<NotFoundPage />} />
  </Routes>;
}
