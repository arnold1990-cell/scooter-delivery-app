import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import deliveriesApi from '../../api/deliveries';

type CreateDeliveryForm = {
  pickupAddress: string;
  dropoffAddress: string;
  pickupLatitude: string;
  pickupLongitude: string;
  dropoffLatitude: string;
  dropoffLongitude: string;
  price: string;
  notes: string;
};

export default function CreateDeliveryPage() {
  const [form, setForm] = useState<CreateDeliveryForm>({
    pickupAddress: '',
    dropoffAddress: '',
    pickupLatitude: '',
    pickupLongitude: '',
    dropoffLatitude: '',
    dropoffLongitude: '',
    price: '',
    notes: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    const payload = {
      pickupAddress: form.pickupAddress,
      dropoffAddress: form.dropoffAddress,
      pickupLatitude: Number(form.pickupLatitude),
      pickupLongitude: Number(form.pickupLongitude),
      dropoffLatitude: Number(form.dropoffLatitude),
      dropoffLongitude: Number(form.dropoffLongitude),
      notes: form.notes || undefined,
      ...(form.price ? { price: Number(form.price) } : {})
    };

    if (
      Number.isNaN(payload.pickupLatitude) ||
      Number.isNaN(payload.pickupLongitude) ||
      Number.isNaN(payload.dropoffLatitude) ||
      Number.isNaN(payload.dropoffLongitude)
    ) {
      setError('Please provide valid pickup/dropoff coordinates.');
      return;
    }

    try {
      await deliveriesApi.create(payload);
      navigate('/customer/my-deliveries');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed creating delivery');
    }
  };

  return (
    <form onSubmit={submit} className="max-w-xl bg-white p-6 rounded shadow space-y-3">
      <input className="w-full border p-2 rounded" placeholder="Pickup Address" onChange={(e) => setForm({ ...form, pickupAddress: e.target.value })} />
      <input className="w-full border p-2 rounded" placeholder="Dropoff Address" onChange={(e) => setForm({ ...form, dropoffAddress: e.target.value })} />

      <div className="grid grid-cols-2 gap-3">
        <input
          className="w-full border p-2 rounded"
          type="number"
          min="-90"
          max="90"
          step="0.000001"
          placeholder="Pickup Latitude"
          onChange={(e) => setForm({ ...form, pickupLatitude: e.target.value })}
        />
        <input
          className="w-full border p-2 rounded"
          type="number"
          min="-180"
          max="180"
          step="0.000001"
          placeholder="Pickup Longitude"
          onChange={(e) => setForm({ ...form, pickupLongitude: e.target.value })}
        />
      </div>

      <div className="grid grid-cols-2 gap-3">
        <input
          className="w-full border p-2 rounded"
          type="number"
          min="-90"
          max="90"
          step="0.000001"
          placeholder="Dropoff Latitude"
          onChange={(e) => setForm({ ...form, dropoffLatitude: e.target.value })}
        />
        <input
          className="w-full border p-2 rounded"
          type="number"
          min="-180"
          max="180"
          step="0.000001"
          placeholder="Dropoff Longitude"
          onChange={(e) => setForm({ ...form, dropoffLongitude: e.target.value })}
        />
      </div>

      <input
        className="w-full border p-2 rounded"
        type="number"
        min="0"
        step="0.01"
        placeholder="Price (optional)"
        onChange={(e) => setForm({ ...form, price: e.target.value })}
      />
      <textarea className="w-full border p-2 rounded" placeholder="Notes" onChange={(e) => setForm({ ...form, notes: e.target.value })} />
      {error && <p className="text-red-500 text-sm">{error}</p>}
      <button className="bg-blue-600 text-white px-4 py-2 rounded">Submit</button>
    </form>
  );
}
