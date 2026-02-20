import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import deliveriesApi from '../../api/deliveries';
import LiveLocationPicker, { type PickedLocation } from '../../components/LiveLocationPicker';

type CreateDeliveryForm = {
  pickupAddress: string;
  dropoffAddress: string;
  price: string;
  notes: string;
};

type PickerMode = 'pickup' | 'dropoff' | null;

export default function CreateDeliveryPage() {
  const [form, setForm] = useState<CreateDeliveryForm>({
    pickupAddress: '',
    dropoffAddress: '',
    price: '',
    notes: ''
  });
  const [pickupLocation, setPickupLocation] = useState<PickedLocation | null>(null);
  const [dropoffLocation, setDropoffLocation] = useState<PickedLocation | null>(null);
  const [pickerMode, setPickerMode] = useState<PickerMode>(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!pickupLocation || !dropoffLocation) {
      setError('Please select both pickup and dropoff locations on the map before submitting.');
      return;
    }

    const payload = {
      pickupAddress: form.pickupAddress || pickupLocation.address || `${pickupLocation.lat}, ${pickupLocation.lng}`,
      dropoffAddress: form.dropoffAddress || dropoffLocation.address || `${dropoffLocation.lat}, ${dropoffLocation.lng}`,
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

  const openPicker = (mode: Exclude<PickerMode, null>) => {
    setError('');
    setPickerMode(mode);
  };

  return (
    <>
      <form onSubmit={submit} className="max-w-3xl bg-white p-6 rounded shadow space-y-4">
        <p className="text-lg font-semibold text-slate-800">Create Delivery</p>

        <div className="space-y-2 rounded border border-slate-200 p-4">
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              className="rounded bg-slate-800 px-3 py-2 text-sm font-medium text-white"
              onClick={() => openPicker('pickup')}
            >
              Select Pickup on Map
            </button>
            {pickupLocation && (
              <button
                type="button"
                className="rounded border border-slate-300 px-3 py-2 text-sm text-slate-700"
                onClick={() => setPickupLocation(null)}
              >
                Clear Pickup
              </button>
            )}
          </div>
          <input
            className="w-full border p-2 rounded"
            placeholder="Pickup Address"
            value={form.pickupAddress}
            onChange={(e) => setForm({ ...form, pickupAddress: e.target.value })}
          />
          {pickupLocation ? (
            <p className="text-sm text-slate-700">
              {pickupLocation.address || 'Address unavailable'} ({pickupLocation.lat.toFixed(6)},{' '}
              {pickupLocation.lng.toFixed(6)})
            </p>
          ) : (
            <p className="text-sm text-slate-500">No pickup selected yet.</p>
          )}
        </div>

        <div className="space-y-2 rounded border border-slate-200 p-4">
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              className="rounded bg-slate-800 px-3 py-2 text-sm font-medium text-white"
              onClick={() => openPicker('dropoff')}
            >
              Select Dropoff on Map
            </button>
            {dropoffLocation && (
              <button
                type="button"
                className="rounded border border-slate-300 px-3 py-2 text-sm text-slate-700"
                onClick={() => setDropoffLocation(null)}
              >
                Clear Dropoff
              </button>
            )}
          </div>
          <input
            className="w-full border p-2 rounded"
            placeholder="Dropoff Address"
            value={form.dropoffAddress}
            onChange={(e) => setForm({ ...form, dropoffAddress: e.target.value })}
          />
          {dropoffLocation ? (
            <p className="text-sm text-slate-700">
              {dropoffLocation.address || 'Address unavailable'} ({dropoffLocation.lat.toFixed(6)},{' '}
              {dropoffLocation.lng.toFixed(6)})
            </p>
          ) : (
            <p className="text-sm text-slate-500">No dropoff selected yet.</p>
          )}
        </div>

        <input
          className="w-full border p-2 rounded"
          type="number"
          min="0"
          step="0.01"
          placeholder="Price (optional)"
          value={form.price}
          onChange={(e) => setForm({ ...form, price: e.target.value })}
        />
        <textarea
          className="w-full border p-2 rounded"
          placeholder="Notes"
          value={form.notes}
          onChange={(e) => setForm({ ...form, notes: e.target.value })}
        />
        {error && <p className="text-red-500 text-sm">{error}</p>}
        <button className="bg-blue-600 text-white px-4 py-2 rounded">Submit</button>
      </form>

      {pickerMode === 'pickup' && (
        <LiveLocationPicker
          title="Pickup location"
          initialValue={pickupLocation}
          onCancel={() => setPickerMode(null)}
          onConfirm={(value) => {
            setPickupLocation(value);
            if (!form.pickupAddress && value.address) {
              setForm((prev) => ({ ...prev, pickupAddress: value.address ?? '' }));
            }
            setPickerMode(null);
          }}
        />
      )}

      {pickerMode === 'dropoff' && (
        <LiveLocationPicker
          title="Dropoff location"
          initialValue={dropoffLocation}
          onCancel={() => setPickerMode(null)}
          onConfirm={(value) => {
            setDropoffLocation(value);
            if (!form.dropoffAddress && value.address) {
              setForm((prev) => ({ ...prev, dropoffAddress: value.address ?? '' }));
            }
            setPickerMode(null);
          }}
        />
      )}
    </>
  );
}
