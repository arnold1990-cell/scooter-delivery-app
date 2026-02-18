import { useEffect, useState } from 'react';
import deliveriesApi from '../../api/deliveries';
import type { Delivery } from '../../types';

const color: Record<string, string> = {
  REQUESTED: 'bg-yellow-100 text-yellow-700',
  ASSIGNED: 'bg-indigo-100 text-indigo-700',
  ACCEPTED: 'bg-blue-100 text-blue-700',
  PICKED_UP: 'bg-purple-100 text-purple-700',
  IN_TRANSIT: 'bg-orange-100 text-orange-700',
  DELIVERED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-slate-100 text-slate-700',
  REJECTED: 'bg-red-100 text-red-700'
};

export default function MyDeliveriesPage() {
  const [deliveries, setDeliveries] = useState<Delivery[]>([]);

  useEffect(() => {
    deliveriesApi.my().then(setDeliveries).catch(() => undefined);
  }, []);

  return (
    <div className="bg-white p-4 rounded shadow overflow-x-auto">
      <table className="w-full text-sm">
        <thead><tr><th>Pickup</th><th>Dropoff</th><th>Price</th><th>Status</th><th>Date</th></tr></thead>
        <tbody>
          {deliveries.map((d) => (
            <tr key={d.id} className="border-t">
              <td>{d.pickupAddress}</td><td>{d.dropoffAddress}</td><td>${d.price}</td>
              <td><span className={`px-2 py-1 rounded ${color[d.status]}`}>{d.status}</span></td>
              <td>{new Date(d.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
