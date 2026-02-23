import { Navigate } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';
import type { Authority } from '../types';

export default function RequireRole({ allowed, children }: { allowed: Authority[]; children: JSX.Element }) {
  const { user } = useAuth();
  const requiredRole = allowed[0];

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const allowedUser = user.authorities.some((authority) => allowed.includes(authority));
  if (!allowedUser) {
    if (import.meta.env.DEV) {
      console.debug('[auth] portal guard block', {
        requiredRole,
        userAuthorities: user.authorities
      });
    }

    return (
      <div className="rounded border border-red-200 bg-red-50 p-4 text-red-700">
        <p className="text-lg font-semibold">Access denied</p>
        <p className="text-sm">You are not allowed to access this portal. {requiredRole} required.</p>
      </div>
    );
  }

  return children;
}
