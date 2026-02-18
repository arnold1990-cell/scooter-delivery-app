import { useEffect, useState } from 'react';
import deliveriesApi from '../../api/deliveries';
import type { Delivery } from '../../types';

export default function AvailableJobsPage() {
  const [jobs, setJobs] = useState<Delivery[]>([]);

  const load = () => deliveriesApi.jobs().then(setJobs).catch(() => undefined);
  useEffect(() => { load(); }, []);

  const accept = async (id: string) => {
    await deliveriesApi.updateStatus(id, 'ACCEPTED');
    load();
  };

  return (
    <div className="grid gap-3">
      {jobs.map((j) => (
        <div key={j.id} className="bg-white p-4 rounded shadow flex justify-between">
          <div><p>{j.pickupAddress} → {j.dropoffAddress}</p><p>${j.price}</p></div>
          <button className="bg-blue-600 text-white px-3 py-1 rounded" onClick={() => accept(j.id)}>Accept Job</button>
        </div>
      ))}
    </div>
  );
}
