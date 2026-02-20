import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';
import ProtectedRoute from './ProtectedRoute';
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
import CustomerRoute from './CustomerRoute';
import RiderRoute from './RiderRoute';
import AdminRoute from './AdminRoute';

export default function AppRoutes() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<CustomerRoute />}>
          <Route path="/customer" element={<CustomerLayout />}>
            <Route path="dashboard" element={<CustomerDashboard />} />
            <Route path="create" element={<CreateDeliveryPage />} />
            <Route path="my-deliveries" element={<MyDeliveriesPage />} />
          </Route>
        </Route>

        <Route element={<RiderRoute />}>
          <Route path="/rider" element={<RiderLayout />}>
            <Route path="dashboard" element={<RiderDashboard />} />
            <Route path="deliveries" element={<RiderDeliveriesPage />} />
            <Route path="available-jobs" element={<AvailableJobsPage />} />
            <Route path="active-delivery" element={<ActiveDeliveryPage />} />
          </Route>
        </Route>

        <Route element={<AdminRoute />}>
          <Route path="/admin" element={<AdminLayout />}>
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
