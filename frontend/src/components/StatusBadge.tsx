import type { DeliveryStatus } from '../types';

const statusColors: Record<DeliveryStatus, string> = {
  CREATED: 'bg-slate-100 text-slate-700',
  ASSIGNED: 'bg-blue-100 text-blue-700',
  ACCEPTED: 'bg-indigo-100 text-indigo-700',
  IN_PROGRESS: 'bg-amber-100 text-amber-700',
  DELIVERED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700'
};

export default function StatusBadge({ status }: { status: DeliveryStatus }) {
  return <span className={`px-2 py-1 rounded-full text-xs font-semibold ${statusColors[status]}`}>{status}</span>;
}
