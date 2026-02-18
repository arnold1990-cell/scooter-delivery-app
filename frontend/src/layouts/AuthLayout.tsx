import { Outlet } from 'react-router-dom';

export default function AuthLayout() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100 p-4">
      <div className="w-full max-w-md rounded-lg bg-white shadow p-6">
        <h1 className="text-2xl font-bold mb-4 text-center">Scooter Delivery</h1>
        <Outlet />
      </div>
    </div>
  );
}
