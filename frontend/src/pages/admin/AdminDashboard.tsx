import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import deliveryService from '../../api/deliveryService';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ total: 0, assigned: 0, inProgress: 0, delivered: 0, pendingRiders: 0 });

  useEffect(() => {
    Promise.all([ridersApi.all(), deliveryService.getAllDeliveries()])
      .then(([riders, deliveries]) => {
        setStats({
          total: deliveries.length,
          assigned: deliveries.filter((d: any) => d.status === 'ASSIGNED').length,
          inProgress: deliveries.filter((d: any) => d.status === 'IN_PROGRESS').length,
          delivered: deliveries.filter((d: any) => d.status === 'DELIVERED').length,
          pendingRiders: riders.filter((r) => r.approvalStatus === 'PENDING').length
        });
      })
      .catch(() => undefined);
  }, []);

  return (
    <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
      <div className="bg-white p-4 rounded shadow">Total: {stats.total}</div>
      <div className="bg-white p-4 rounded shadow">Assigned: {stats.assigned}</div>
      <div className="bg-white p-4 rounded shadow">In Progress: {stats.inProgress}</div>
      <div className="bg-white p-4 rounded shadow">Delivered: {stats.delivered}</div>
      <div className="bg-white p-4 rounded shadow">Pending Riders: {stats.pendingRiders}</div>
    </div>
  );
}
