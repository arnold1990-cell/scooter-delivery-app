import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';
import type { UserRole } from '../types';

export default function RoleRoute({ allowedRoles }: { allowedRoles: UserRole[] }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (!user.roles.some((role) => allowedRoles.includes(role))) return <Navigate to="/" replace />;
  return <Outlet />;
}
