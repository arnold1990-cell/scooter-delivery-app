import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import deliveriesApi from '../../api/deliveries';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ users: 0, pendingRiders: 0, deliveries: 0 });

  useEffect(() => {
    Promise.all([ridersApi.all(), deliveriesApi.adminAll()]).then(([riders, deliveries]) => {
      setStats({ users: riders.length, pendingRiders: riders.filter((r) => r.approvalStatus === 'PENDING').length, deliveries: deliveries.length });
    }).catch(() => undefined);
  }, []);

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div className="bg-white p-4 rounded shadow">Total users: {stats.users}</div>
      <div className="bg-white p-4 rounded shadow">Pending riders: {stats.pendingRiders}</div>
      <div className="bg-white p-4 rounded shadow">Total deliveries: {stats.deliveries}</div>
    </div>
  );
}
