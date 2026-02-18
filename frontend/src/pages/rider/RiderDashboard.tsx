import { useEffect, useState } from 'react';
import ridersApi from '../../api/riders';
import type { RiderProfile } from '../../types';

export default function RiderDashboard() {
  const [profile, setProfile] = useState<RiderProfile | null>(null);

  useEffect(() => {
    ridersApi.me().then(setProfile).catch(() => undefined);
  }, []);

  if (!profile) return <p>Loading...</p>;

  return (
    <div className="space-y-4">
      {profile.approvalStatus === 'PENDING' && <div className="bg-yellow-100 text-yellow-800 p-3 rounded">Awaiting admin approval</div>}
      <div className="bg-white p-4 rounded shadow">
        <p>Status: <strong>{profile.approvalStatus}</strong></p>
        <label className="flex items-center gap-2 mt-2">
          <input type="checkbox" checked={profile.isOnline} onChange={async (e) => setProfile(await ridersApi.toggleOnline(e.target.checked))} />
          Online
        </label>
      </div>
    </div>
  );
}
