import { Navigate } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';
import type { UserRole } from '../types';

export default function RequireRole({ allowed, children }: { allowed: UserRole[]; children: JSX.Element }) {
  const { user } = useAuth();
  const requiredRole = allowed[0];

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (import.meta.env.DEV) {
    console.debug('[auth] portal guard check', {
      requiredRole,
      userRoles: user.roles
    });
  }

  const allowedUser = user.roles.some((role) => allowed.includes(role));
  if (!allowedUser) {
    return (
      <div className="rounded border border-red-200 bg-red-50 p-4 text-red-700">
        <p className="text-lg font-semibold">Access denied</p>
        <p className="text-sm">
          You are not allowed to access this portal. {requiredRole.charAt(0) + requiredRole.slice(1).toLowerCase()} role required.
        </p>
      </div>
    );
  }

  return children;
}
