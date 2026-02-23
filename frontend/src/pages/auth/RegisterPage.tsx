import { useState } from 'react';
import { isHttpError } from '../../api/http';
import { Link, useNavigate } from 'react-router-dom';
import { ROLES } from '../../constants/roles';
import { useAuth } from '../../store/AuthContext';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', password: '', role: ROLES.CUSTOMER as 'CUSTOMER' | 'RIDER' });
  const [error, setError] = useState('');

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const user = await register(form);
      const path = user.roles.includes('CUSTOMER') ? '/customer/dashboard' : '/rider/dashboard';
      navigate(path);
    } catch (err: unknown) {
      if (!isHttpError(err)) {
        setError('Cannot reach backend server. Check API URL or CORS.');
        return;
      }

      const errorData = err.response?.data as { message?: string; fieldErrors?: Record<string, string> } | undefined;
      const fieldValidationMessage = errorData?.fieldErrors ? Object.values(errorData.fieldErrors).join(', ') : undefined;
      setError(errorData?.message || fieldValidationMessage || 'Registration failed');
    }
  };

  return (
    <form onSubmit={submit} className="space-y-3">
      <input className="w-full border p-2 rounded" placeholder="Full name" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
      <input className="w-full border p-2 rounded" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
      <input className="w-full border p-2 rounded" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
      <select className="w-full border p-2 rounded" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value as 'CUSTOMER' | 'RIDER' })}>
        <option value="CUSTOMER">Customer</option>
        <option value="RIDER">Rider</option>
      </select>
      {error && <p className="text-red-500 text-sm">{error}</p>}
      <button className="w-full bg-blue-600 text-white p-2 rounded">Register</button>
      <p className="text-sm">Already have account? <Link className="text-blue-600" to="/login">Login</Link></p>
    </form>
  );
}
