import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import type { RiderProfile } from '../../types';

export default function AdminRidersPage() {
  const [riders, setRiders] = useState<RiderProfile[]>([]);
  const load = () => ridersApi.all().then(setRiders).catch(() => undefined);
  useEffect(() => { load(); }, []);

  return (
    <div className="bg-white p-4 rounded shadow overflow-x-auto">
      <table className="w-full text-sm">
        <thead><tr><th>Name</th><th>Email</th><th>License</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          {riders.map((r) => (
            <tr key={r.id} className="border-t">
              <td>{r.fullName}</td><td>{r.email}</td><td>{r.licenseNumber || '-'}</td><td>{r.approvalStatus}</td>
              <td className="space-x-2">
                <button className="bg-green-600 text-white px-2 py-1 rounded" onClick={async () => { await ridersApi.approve(r.userId, 'APPROVED'); load(); }}>Approve</button>
                <button className="bg-red-600 text-white px-2 py-1 rounded" onClick={async () => { await ridersApi.approve(r.userId, 'REJECTED'); load(); }}>Reject</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
