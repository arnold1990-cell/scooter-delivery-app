import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import deliveriesApi from '../../api/deliveries';
import LocationPickerMap, { type LocationValue } from '../../components/LocationPickerMap';

type CreateDeliveryForm = {
  pickupAddress: string;
  dropoffAddress: string;
  price: string;
  notes: string;
};

export default function CreateDeliveryPage() {
  const [form, setForm] = useState<CreateDeliveryForm>({
    pickupAddress: '',
    dropoffAddress: '',
    price: '',
    notes: ''
  });
  const [pickupLocation, setPickupLocation] = useState<LocationValue | null>(null);
  const [dropoffLocation, setDropoffLocation] = useState<LocationValue | null>(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!pickupLocation || !dropoffLocation) {
      setError('Please select both pickup and dropoff locations on the map before submitting.');
      return;
    }

    const payload = {
      pickupAddress: form.pickupAddress,
      dropoffAddress: form.dropoffAddress,
      pickupLatitude: pickupLocation.lat,
      pickupLongitude: pickupLocation.lng,
      dropoffLatitude: dropoffLocation.lat,
      dropoffLongitude: dropoffLocation.lng,
      notes: form.notes || undefined,
      ...(form.price ? { price: Number(form.price) } : {})
    };

    try {
      await deliveriesApi.create(payload);
      navigate('/customer/my-deliveries');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed creating delivery');
    }
  };

  return (
    <form onSubmit={submit} className="max-w-3xl bg-white p-6 rounded shadow space-y-4">
      <input
        className="w-full border p-2 rounded"
        placeholder="Pickup Address"
        onChange={(e) => setForm({ ...form, pickupAddress: e.target.value })}
      />
      <input
        className="w-full border p-2 rounded"
        placeholder="Dropoff Address"
        onChange={(e) => setForm({ ...form, dropoffAddress: e.target.value })}
      />

      <LocationPickerMap
        label="Select Pickup Location"
        value={pickupLocation}
        onChange={(value) => {
          setError('');
          setPickupLocation(value);
        }}
      />
      <button
        type="button"
        className="rounded border border-slate-300 px-3 py-1 text-sm text-slate-700 hover:bg-slate-50"
        onClick={() => setPickupLocation(null)}
      >
        Clear Pickup
      </button>

      <LocationPickerMap
        label="Select Dropoff Location"
        value={dropoffLocation}
        onChange={(value) => {
          setError('');
          setDropoffLocation(value);
        }}
      />
      <button
        type="button"
        className="rounded border border-slate-300 px-3 py-1 text-sm text-slate-700 hover:bg-slate-50"
        onClick={() => setDropoffLocation(null)}
      >
        Clear Dropoff
      </button>

      <input
        className="w-full border p-2 rounded"
        type="number"
        min="0"
        step="0.01"
        placeholder="Price (optional)"
        onChange={(e) => setForm({ ...form, price: e.target.value })}
      />
      <textarea
        className="w-full border p-2 rounded"
        placeholder="Notes"
        onChange={(e) => setForm({ ...form, notes: e.target.value })}
      />
      {error && <p className="text-red-500 text-sm">{error}</p>}
      <button className="bg-blue-600 text-white px-4 py-2 rounded">Submit</button>
    </form>
  );
}
