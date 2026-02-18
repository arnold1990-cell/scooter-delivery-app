import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';
import ProtectedRoute from './ProtectedRoute';
import RoleRoute from './RoleRoute';
import AuthLayout from '../layouts/AuthLayout';
import CustomerLayout from '../layouts/CustomerLayout';
import RiderLayout from '../layouts/RiderLayout';
import AdminLayout from '../layouts/AdminLayout';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import CustomerDashboard from '../pages/customer/CustomerDashboard';
import CreateDeliveryPage from '../pages/customer/CreateDeliveryPage';
import MyDeliveriesPage from '../pages/customer/MyDeliveriesPage';
import RiderDashboard from '../pages/rider/RiderDashboard';
import AvailableJobsPage from '../pages/rider/AvailableJobsPage';
import ActiveDeliveryPage from '../pages/rider/ActiveDeliveryPage';
import AdminDashboard from '../pages/admin/AdminDashboard';
import AdminRidersPage from '../pages/admin/AdminRidersPage';
import AdminDeliveriesPage from '../pages/admin/AdminDeliveriesPage';

export default function AppRoutes() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<RoleRoute allowedRoles={['CUSTOMER']} />}>
          <Route path="/customer" element={<CustomerLayout />}>
            <Route path="dashboard" element={<CustomerDashboard />} />
            <Route path="create" element={<CreateDeliveryPage />} />
            <Route path="my-deliveries" element={<MyDeliveriesPage />} />
          </Route>
        </Route>

        <Route element={<RoleRoute allowedRoles={['RIDER']} />}>
          <Route path="/rider" element={<RiderLayout />}>
            <Route path="dashboard" element={<RiderDashboard />} />
            <Route path="jobs" element={<AvailableJobsPage />} />
            <Route path="active" element={<ActiveDeliveryPage />} />
          </Route>
        </Route>

        <Route element={<RoleRoute allowedRoles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="riders" element={<AdminRidersPage />} />
            <Route path="deliveries" element={<AdminDeliveriesPage />} />
          </Route>
        </Route>
      </Route>

      <Route
        path="/"
        element={
          user ? (
            <Navigate to={user.role === 'CUSTOMER' ? '/customer/dashboard' : user.role === 'RIDER' ? '/rider/dashboard' : '/admin/dashboard'} replace />
          ) : (
            <Navigate to="/login" replace />
          )
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
