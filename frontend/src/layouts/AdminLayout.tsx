import { Link, NavLink, Outlet, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import { useAuth } from '../store/AuthContext';

const navItems = [
  { label: 'Dashboard', to: '/admin/dashboard' },
  { label: 'Riders', to: '/admin/riders' },
  { label: 'Orders', to: '/admin/orders' },
  { label: 'Pricing Zones', to: '/admin/pricing-zones' },
  { label: 'Disputes', to: '/admin/disputes' },
  { label: 'Analytics', to: '/admin/analytics' },
  { label: 'Settings', to: '/admin/settings' }
];

export default function AdminLayout() {
  const { logout } = useAuth();
  const location = useLocation();

  useEffect(() => {
    console.log('[Route Debug] pathname:', location.pathname);
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="sticky top-0 z-20 border-b border-slate-200 bg-yellow-300 px-6 py-4 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-2xl font-black tracking-wide">ADMIN APP</p>
            <p className="text-sm font-medium">Current route: {location.pathname}</p>
          </div>
          <div className="flex items-center gap-2">
            <Link to="/admin/dashboard" className="rounded bg-slate-900 px-3 py-2 text-sm font-semibold text-white">
              Home
            </Link>
            <button onClick={logout} className="rounded bg-red-600 px-3 py-2 text-sm font-semibold text-white">
              Logout
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto grid max-w-7xl grid-cols-1 gap-4 p-4 md:grid-cols-[250px_1fr]">
        <aside className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
          <p className="mb-3 text-xs font-bold uppercase text-slate-500">Admin Navigation</p>
          <nav className="flex flex-col gap-2">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `rounded px-3 py-2 text-sm font-semibold transition ${
                    isActive ? 'bg-slate-900 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="mt-6 rounded border border-blue-200 bg-blue-50 p-3 text-xs text-blue-900">
            <p className="font-bold">Route Debug</p>
            <p>pathname: {location.pathname}</p>
          </div>
        </aside>

        <main className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
