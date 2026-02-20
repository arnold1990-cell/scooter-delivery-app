import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import type { RiderProfile } from '../../types';
import AdminPageTitle from './AdminPageTitle';

export default function AdminRidersPage() {
  const [riders, setRiders] = useState<RiderProfile[]>([]);
  const load = () => ridersApi.all().then(setRiders).catch(() => undefined);

  useEffect(() => {
    load();
  }, []);

  return (
    <AdminPageTitle title="Riders">
      <div className="overflow-x-auto rounded border border-slate-200 bg-white p-4 shadow">
        <table className="w-full text-sm">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>License</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {riders.map((r) => (
              <tr key={r.id} className="border-t">
                <td>{r.fullName}</td>
                <td>{r.email}</td>
                <td>{r.licenseNumber || '-'}</td>
                <td>{r.approvalStatus}</td>
                <td className="space-x-2">
                  <button
                    className="rounded bg-green-600 px-2 py-1 text-white"
                    onClick={async () => {
                      await ridersApi.approve(r.userId, 'APPROVED');
                      load();
                    }}
                  >
                    Approve
                  </button>
                  <button
                    className="rounded bg-red-600 px-2 py-1 text-white"
                    onClick={async () => {
                      await ridersApi.approve(r.userId, 'REJECTED');
                      load();
                    }}
                  >
                    Reject
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminPageTitle>
  );
}
