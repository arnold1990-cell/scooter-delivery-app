import { useEffect, useState } from 'react';
import deliveriesApi from '../../api/deliveries';
import type { Delivery } from '../../types';

export default function AdminDeliveriesPage() {
  const [deliveries, setDeliveries] = useState<Delivery[]>([]);

  useEffect(() => {
    deliveriesApi.adminAll().then(setDeliveries).catch(() => undefined);
  }, []);

  return (
    <div className="bg-white p-4 rounded shadow overflow-x-auto">
      <table className="w-full text-sm">
        <thead><tr><th>ID</th><th>Customer</th><th>Rider</th><th>Pickup</th><th>Dropoff</th><th>Status</th><th>Date</th></tr></thead>
        <tbody>
          {deliveries.map((d) => (
            <tr key={d.id} className="border-t">
              <td>{d.id.slice(0, 8)}...</td><td>{d.customerId.slice(0, 8)}...</td><td>{d.riderId?.slice(0, 8) || '-'}</td>
              <td>{d.pickupAddress}</td><td>{d.dropoffAddress}</td><td>{d.status}</td><td>{new Date(d.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
