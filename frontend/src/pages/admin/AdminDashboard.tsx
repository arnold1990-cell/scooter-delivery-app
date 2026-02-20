import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import deliveryService from '../../api/deliveryService';
import AdminPageTitle from './AdminPageTitle';

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
    <AdminPageTitle title="Dashboard">
      <div className="grid grid-cols-1 gap-4 md:grid-cols-5">
        <div className="rounded border border-slate-200 bg-white p-4 shadow">Total Orders: {stats.total}</div>
        <div className="rounded border border-slate-200 bg-white p-4 shadow">Assigned: {stats.assigned}</div>
        <div className="rounded border border-slate-200 bg-white p-4 shadow">In Progress: {stats.inProgress}</div>
        <div className="rounded border border-slate-200 bg-white p-4 shadow">Delivered: {stats.delivered}</div>
        <div className="rounded border border-slate-200 bg-white p-4 shadow">Pending Riders: {stats.pendingRiders}</div>
      </div>
    </AdminPageTitle>
  );
}
