import { Navigate } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';
import type { UserRole } from '../types';

export default function RequireRole({ allowed, children }: { allowed: UserRole[]; children: JSX.Element }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const allowedUser = user.roles.some((role) => allowed.includes(role));
  if (!allowedUser) {
    return (
      <div className="rounded border border-red-200 bg-red-50 p-4 text-red-700">
        <p className="text-lg font-semibold">Access denied</p>
        <p className="text-sm">You do not have permission to view this area.</p>
      </div>
    );
  }

  return children;
}
