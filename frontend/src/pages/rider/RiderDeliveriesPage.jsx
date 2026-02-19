import { useEffect, useState } from 'react';
import deliveryService from '../../api/deliveryService';
import StatusBadge from '../../components/StatusBadge';

const nextActionByStatus = {
  ASSIGNED: 'ACCEPTED',
  ACCEPTED: 'IN_PROGRESS',
  IN_PROGRESS: 'DELIVERED'
};

export default function RiderDeliveriesPage() {
  const [deliveries, setDeliveries] = useState([]);

  const load = async () => {
    const data = await deliveryService.getAssignedDeliveries();
    setDeliveries(data);
  };

  useEffect(() => {
    load().catch(() => undefined);
  }, []);

  const moveStatus = async (deliveryId, nextStatus) => {
    await deliveryService.updateStatus(deliveryId, nextStatus);
    await load();
  };

  return (
    <div className="space-y-3">
      {deliveries.map((delivery) => {
        const nextStatus = nextActionByStatus[delivery.status];
        return (
          <div key={delivery.id} className="bg-white p-4 rounded shadow flex items-center justify-between gap-4">
            <div>
              <p className="font-semibold">{delivery.pickupAddress} → {delivery.dropoffAddress}</p>
              <p className="text-sm text-slate-500">Delivery #{delivery.id.slice(0, 8)}</p>
              <div className="mt-2"><StatusBadge status={delivery.status} /></div>
            </div>

            {nextStatus ? (
              <button
                className="bg-blue-600 text-white px-3 py-2 rounded"
                onClick={() => moveStatus(delivery.id, nextStatus)}
              >
                Mark {nextStatus}
              </button>
            ) : (
              <span className="text-sm text-slate-500">No action available</span>
            )}
          </div>
        );
      })}

      {!deliveries.length && <p className="text-slate-600">No assigned deliveries yet.</p>}
    </div>
  );
}
