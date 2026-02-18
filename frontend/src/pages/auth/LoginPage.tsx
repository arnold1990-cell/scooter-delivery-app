import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../store/AuthContext';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const user = await login({ email, password });
      const path = user.role === 'CUSTOMER' ? '/customer/dashboard' : user.role === 'RIDER' ? '/rider/dashboard' : '/admin/dashboard';
      navigate(path);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    }
  };

  return (
    <form onSubmit={submit} className="space-y-4">
      <input className="w-full border p-2 rounded" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input className="w-full border p-2 rounded" placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      {error && <p className="text-red-500 text-sm">{error}</p>}
      <button className="w-full bg-blue-600 text-white p-2 rounded">Login</button>
      <p className="text-sm">No account? <Link className="text-blue-600" to="/register">Register</Link></p>
    </form>
  );
}
