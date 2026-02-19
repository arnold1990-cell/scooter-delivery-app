import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';

export default function AdminLayout() {
  const { logout } = useAuth();
  return (
    <div className="min-h-screen">
      <nav className="bg-slate-900 text-white p-4 flex justify-between">
        <div className="font-bold">Scooter Delivery</div>
        <div className="space-x-4">
          <Link to="/admin/dashboard">Dashboard</Link>
          <Link to="/admin/deliveries">Manage Deliveries</Link>
          <button onClick={logout} className="bg-red-600 px-3 py-1 rounded">Logout</button>
        </div>
      </nav>
      <main className="p-6"><Outlet /></main>
    </div>
  );
}
