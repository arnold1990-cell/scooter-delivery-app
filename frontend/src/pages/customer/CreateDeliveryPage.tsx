import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import deliveriesApi from '../../api/deliveries';

export default function CreateDeliveryPage() {
  const [form, setForm] = useState({ pickupAddress: '', dropoffAddress: '', price: 0, notes: '' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await deliveriesApi.create(form);
      navigate('/customer/my-deliveries');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed creating delivery');
    }
  };

  return (
    <form onSubmit={submit} className="max-w-xl bg-white p-6 rounded shadow space-y-3">
      <input className="w-full border p-2 rounded" placeholder="Pickup Address" onChange={(e) => setForm({ ...form, pickupAddress: e.target.value })} />
      <input className="w-full border p-2 rounded" placeholder="Dropoff Address" onChange={(e) => setForm({ ...form, dropoffAddress: e.target.value })} />
      <input className="w-full border p-2 rounded" type="number" min="0" step="0.01" placeholder="Price" onChange={(e) => setForm({ ...form, price: Number(e.target.value) })} />
      <textarea className="w-full border p-2 rounded" placeholder="Notes" onChange={(e) => setForm({ ...form, notes: e.target.value })} />
      {error && <p className="text-red-500 text-sm">{error}</p>}
      <button className="bg-blue-600 text-white px-4 py-2 rounded">Submit</button>
    </form>
  );
}
