import { useEffect, useMemo, useState } from 'react';
import deliveryService from '../../api/deliveryService';
import ridersApi from '../../api/riders';
import StatusBadge from '../../components/StatusBadge';
import type { Delivery, DeliveryStatus, RiderProfile } from '../../types';
import AdminPageTitle from './AdminPageTitle';

const statusOptions: DeliveryStatus[] = ['CREATED', 'ASSIGNED', 'ACCEPTED', 'IN_PROGRESS', 'DELIVERED', 'CANCELLED'];

export default function AdminDeliveriesPage() {
  const [deliveries, setDeliveries] = useState<Delivery[]>([]);
  const [riders, setRiders] = useState<RiderProfile[]>([]);

  const approvedRiders = useMemo(() => riders.filter((r) => r.approvalStatus === 'APPROVED'), [riders]);

  const load = async () => {
    const [allDeliveries, allRiders] = await Promise.all([deliveryService.getAllDeliveries(), ridersApi.all()]);
    setDeliveries(allDeliveries);
    setRiders(allRiders);
  };

  useEffect(() => {
    load().catch(() => undefined);
  }, []);

  const assign = async (deliveryId: string, riderId: string) => {
    if (!riderId) return;
    await deliveryService.assignDelivery(deliveryId, riderId);
    await load();
  };

  const updateStatus = async (deliveryId: string, status: DeliveryStatus) => {
    await deliveryService.updateStatus(deliveryId, status);
    await load();
  };

  return (
    <AdminPageTitle title="Orders">
      <div className="overflow-x-auto rounded border border-slate-200 bg-white p-4 shadow">
        <table className="w-full text-sm">
          <thead>
            <tr>
              <th className="p-2 text-left">Order</th>
              <th className="p-2 text-left">Route</th>
              <th className="p-2 text-left">Rider</th>
              <th className="p-2 text-left">Status</th>
              <th className="p-2 text-left">Change Status</th>
            </tr>
          </thead>
          <tbody>
            {deliveries.map((delivery) => (
              <tr key={delivery.id} className="border-t">
                <td className="p-2">#{delivery.id.slice(0, 8)}</td>
                <td className="p-2">
                  {delivery.pickupAddress} → {delivery.dropoffAddress}
                </td>
                <td className="p-2">
                  <select
                    className="rounded border px-2 py-1"
                    value={delivery.riderId || ''}
                    onChange={(e) => assign(delivery.id, e.target.value)}
                  >
                    <option value="">Unassigned</option>
                    {approvedRiders.map((rider) => (
                      <option key={rider.id} value={rider.userId}>
                        {rider.fullName || rider.email || rider.userId}
                      </option>
                    ))}
                  </select>
                </td>
                <td className="p-2">
                  <StatusBadge status={delivery.status} />
                </td>
                <td className="p-2">
                  <select
                    className="rounded border px-2 py-1"
                    value={delivery.status}
                    onChange={(e) => updateStatus(delivery.id, e.target.value as DeliveryStatus)}
                  >
                    {statusOptions.map((status) => (
                      <option key={status} value={status}>
                        {status}
                      </option>
                    ))}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminPageTitle>
  );
}
