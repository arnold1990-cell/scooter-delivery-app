import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import deliveriesApi from '../../api/deliveries';
import { useAuth } from '../../store/AuthContext';

export default function CustomerDashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState({ total: 0, pending: 0, delivered: 0 });

  useEffect(() => {
    deliveriesApi.my().then((items) => {
      setStats({
        total: items.length,
        pending: items.filter((d) => !['DELIVERED', 'CANCELLED'].includes(d.status)).length,
        delivered: items.filter((d) => d.status === 'DELIVERED').length
      });
    }).catch(() => undefined);
  }, []);

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold">Welcome, {user?.fullName}</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-4 rounded shadow">Total Orders: {stats.total}</div>
        <div className="bg-white p-4 rounded shadow">Pending: {stats.pending}</div>
        <div className="bg-white p-4 rounded shadow">Delivered: {stats.delivered}</div>
      </div>
      <Link to="/customer/create" className="inline-block bg-blue-600 text-white px-4 py-2 rounded">Create Delivery</Link>
    </div>
  );
}
