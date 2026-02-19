import { Navigate, Outlet } from 'react-router-dom';
import { getToken } from '../utils/token';
import { extractRoleFromToken } from '../utils/jwt';

const roleHome = {
  ADMIN: '/admin/dashboard',
  RIDER: '/rider/dashboard',
  CUSTOMER: '/customer/dashboard'
};

export default function RiderRoute() {
  const token = getToken();
  if (!token) return <Navigate to="/login" replace />;

  const role = extractRoleFromToken(token);
  if (!role) return <Navigate to="/login" replace />;
  if (role !== 'RIDER') return <Navigate to={roleHome[role] || '/login'} replace />;

  return <Outlet />;
}
