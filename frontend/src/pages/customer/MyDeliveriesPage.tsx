import { useEffect, useState } from 'react';
import deliveriesApi from '../../api/deliveries';
import StatusBadge from '../../components/StatusBadge';
import type { Delivery } from '../../types';

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
              <td><StatusBadge status={d.status} /></td>
              <td>{new Date(d.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
