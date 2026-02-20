import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../store/AuthContext';

export default function RiderLayout() {
  const { logout } = useAuth();
  return (
    <div className="min-h-screen">
      <nav className="bg-slate-900 text-white p-4 flex justify-between">
        <div className="font-bold">Scooter Delivery</div>
        <div className="space-x-4">
          <Link to="/rider/dashboard">Dashboard</Link>
          <Link to="/rider/deliveries">Assigned Deliveries</Link>
          <Link to="/rider/available-jobs">Available Jobs</Link>
          <Link to="/rider/active-delivery">Active Delivery</Link>
          <button onClick={logout} className="bg-red-600 px-3 py-1 rounded">Logout</button>
        </div>
      </nav>
      <main className="p-6"><Outlet /></main>
    </div>
  );
}
