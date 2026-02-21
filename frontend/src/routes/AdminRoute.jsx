import { Navigate, Outlet } from 'react-router-dom';
import { getToken } from '../utils/token';
import { extractRoleFromToken, tokenHasRole } from '../utils/jwt';

const roleHome = {
  ADMIN: '/admin/dashboard',
  RIDER: '/rider/dashboard',
  CUSTOMER: '/customer/dashboard'
};

export default function AdminRoute() {
  const token = getToken();
  if (!token) return <Navigate to="/login" replace />;

  if (tokenHasRole(token, 'ROLE_ADMIN')) return <Outlet />;

  const role = extractRoleFromToken(token);
  if (!role) return <Navigate to="/login" replace />;
  return <Navigate to={roleHome[role] || '/login'} replace />;
}
