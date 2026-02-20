import { useState } from 'react';
import { isHttpError } from '../../api/http';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../store/AuthContext';
import type { UserRole } from '../../types';

type PortalRole = UserRole;

const PORTAL_OPTIONS: PortalRole[] = ['CUSTOMER', 'RIDER', 'ADMIN'];

const roleHome: Record<PortalRole, string> = {
  CUSTOMER: '/customer/dashboard',
  RIDER: '/rider/dashboard',
  ADMIN: '/admin/dashboard'
};

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [portalRole, setPortalRole] = useState<PortalRole>('CUSTOMER');
  const [error, setError] = useState('');

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const user = await login({ email, password });
      if (!user.roles.includes(portalRole)) {
        setError('You are not allowed to access this portal. Admin role required.');
        return;
      }
      navigate(roleHome[portalRole]);
    } catch (err: unknown) {
      if (!isHttpError(err)) {
        setError('Cannot reach backend server. Check API URL or CORS.');
        return;
      }

      if (err.status === 401) {
        setError('Invalid email/phone or password.');
      } else if (err.status === 403) {
        setError('You are not allowed to access this portal. Admin role required.');
      } else {
        const message = (err.data as { message?: string } | null)?.message;
        setError(message || 'Login failed');
      }
    }
  };

  return (
    <form onSubmit={submit} className="space-y-4">
      <label className="block text-sm font-medium text-slate-700">
        Login as
        <select
          className="mt-1 w-full rounded border p-2"
          value={portalRole}
          onChange={(e) => setPortalRole(e.target.value as PortalRole)}
        >
          {PORTAL_OPTIONS.map((role) => (
            <option key={role} value={role}>
              {role.charAt(0) + role.slice(1).toLowerCase()}
            </option>
          ))}
        </select>
      </label>
      <input className="w-full rounded border p-2" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input className="w-full rounded border p-2" placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      {error && <p className="text-sm text-red-500">{error}</p>}
      <button className="w-full rounded bg-blue-600 p-2 text-white">Login</button>
      <p className="text-sm">
        No account?{' '}
        <Link className="text-blue-600" to="/register">
          Register
        </Link>
      </p>
    </form>
  );
}
