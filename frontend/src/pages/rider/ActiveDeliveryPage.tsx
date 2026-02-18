import { useEffect, useState } from 'react';
import deliveriesApi from '../../api/deliveries';
import type { Delivery } from '../../types';

export default function ActiveDeliveryPage() {
  const [delivery, setDelivery] = useState<Delivery | null>(null);

  const load = async () => {
    const items = await deliveriesApi.active();
    const active = items.find((d) => ['ASSIGNED', 'PICKED_UP', 'IN_TRANSIT'].includes(d.status));
    setDelivery(active || null);
  };

  useEffect(() => { load().catch(() => undefined); }, []);

  if (!delivery) return <p>No active delivery</p>;

  const next = delivery.status === 'ASSIGNED' ? 'PICKED_UP' : delivery.status === 'PICKED_UP' ? 'IN_TRANSIT' : 'DELIVERED';

  return (
    <div className="bg-white p-4 rounded shadow space-y-3">
      <p>{delivery.pickupAddress} → {delivery.dropoffAddress}</p>
      <p>Status: {delivery.status}</p>
      <button
        className="bg-green-600 text-white px-3 py-1 rounded"
        onClick={async () => { await deliveriesApi.updateStatus(delivery.id, next as any); await load(); }}
      >
        Mark {next}
      </button>
    </div>
  );
}
