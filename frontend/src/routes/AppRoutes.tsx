import { Navigate, Route, Routes } from 'react-router-dom';
import { AUTHORITIES } from '../constants/roles';
import { useAuth } from '../store/AuthContext';
import ProtectedRoute from './ProtectedRoute';
import RequireRole from './RequireRole';
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
import RiderDeliveriesPage from '../pages/rider/RiderDeliveriesPage';
import AvailableJobsPage from '../pages/rider/AvailableJobsPage';
import ActiveDeliveryPage from '../pages/rider/ActiveDeliveryPage';
import AdminDashboard from '../pages/admin/AdminDashboard';
import AdminDeliveriesPage from '../pages/admin/AdminDeliveriesPage';
import AdminRidersPage from '../pages/admin/AdminRidersPage';
import AdminPricingZonesPage from '../pages/admin/AdminPricingZonesPage';
import AdminDisputesPage from '../pages/admin/AdminDisputesPage';
import AdminAnalyticsPage from '../pages/admin/AdminAnalyticsPage';
import AdminSettingsPage from '../pages/admin/AdminSettingsPage';

export default function AppRoutes() {
  const { user } = useAuth();
  const homePath = user?.authorities.includes(AUTHORITIES.ADMIN)
    ? '/admin/dashboard'
    : user?.authorities.includes(AUTHORITIES.RIDER)
      ? '/rider/dashboard'
      : '/customer/dashboard';

  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route
          path="/customer"
          element={
            <RequireRole allowed={[AUTHORITIES.CUSTOMER]}>
              <CustomerLayout />
            </RequireRole>
          }
        >
          <Route path="dashboard" element={<CustomerDashboard />} />
          <Route path="create" element={<CreateDeliveryPage />} />
          <Route path="my-deliveries" element={<MyDeliveriesPage />} />
        </Route>

        <Route
          path="/rider"
          element={
            <RequireRole allowed={[AUTHORITIES.RIDER]}>
              <RiderLayout />
            </RequireRole>
          }
        >
          <Route path="dashboard" element={<RiderDashboard />} />
          <Route path="deliveries" element={<RiderDeliveriesPage />} />
          <Route path="available-jobs" element={<AvailableJobsPage />} />
          <Route path="active-delivery" element={<ActiveDeliveryPage />} />
        </Route>

        <Route
          path="/admin"
          element={
            <RequireRole allowed={[AUTHORITIES.ADMIN]}>
              <AdminLayout />
            </RequireRole>
          }
        >
          <Route index element={<Navigate to="dashboard" replace />} />
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="orders" element={<AdminDeliveriesPage />} />
          <Route path="riders" element={<AdminRidersPage />} />
          <Route path="pricing-zones" element={<AdminPricingZonesPage />} />
          <Route path="disputes" element={<AdminDisputesPage />} />
          <Route path="analytics" element={<AdminAnalyticsPage />} />
          <Route path="settings" element={<AdminSettingsPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to={user ? homePath : '/login'} replace />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
