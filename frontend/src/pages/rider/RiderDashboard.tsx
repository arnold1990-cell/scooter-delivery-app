import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import deliveryService from '../../api/deliveryService';
import type { RiderProfile } from '../../types';

export default function RiderDashboard() {
  const [profile, setProfile] = useState<RiderProfile | null>(null);
  const [stats, setStats] = useState({ assigned: 0, inProgress: 0, delivered: 0 });

  useEffect(() => {
    Promise.all([ridersApi.me(), deliveryService.getAssignedDeliveries()])
      .then(([riderProfile, deliveries]) => {
        setProfile(riderProfile);
        setStats({
          assigned: deliveries.filter((d: any) => d.status === 'ASSIGNED').length,
          inProgress: deliveries.filter((d: any) => d.status === 'IN_PROGRESS').length,
          delivered: deliveries.filter((d: any) => d.status === 'DELIVERED').length
        });
      })
      .catch(() => undefined);
  }, []);

  if (!profile) return <p>Loading...</p>;

  return (
    <div className="space-y-4">
      <div className="bg-white p-4 rounded shadow">
        <p>Status: <strong>{profile.approvalStatus}</strong></p>
        <label className="flex items-center gap-2 mt-2">
          <input type="checkbox" checked={profile.isOnline} onChange={async (e) => setProfile(await ridersApi.toggleOnline(e.target.checked))} />
          Online
        </label>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-4 rounded shadow">Assigned: {stats.assigned}</div>
        <div className="bg-white p-4 rounded shadow">In Progress: {stats.inProgress}</div>
        <div className="bg-white p-4 rounded shadow">Delivered: {stats.delivered}</div>
      </div>
    </div>
  );
}
