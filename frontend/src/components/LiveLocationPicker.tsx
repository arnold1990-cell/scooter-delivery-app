import { useEffect, useMemo, useRef, useState } from 'react';
import mapboxgl from 'mapbox-gl';

export type PickedLocation = { lat: number; lng: number; address?: string };

type LiveLocationPickerProps = {
  title: string;
  initialCenter?: { lat: number; lng: number };
  initialValue?: PickedLocation | null;
  onConfirm: (value: PickedLocation) => void;
  onCancel: () => void;
};

const DEFAULT_CENTER = { lat: -24.6282, lng: 25.9231 };
const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_TOKEN as string | undefined;

export default function LiveLocationPicker({
  title,
  initialCenter = DEFAULT_CENTER,
  initialValue = null,
  onConfirm,
  onCancel
}: LiveLocationPickerProps) {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<mapboxgl.Map | null>(null);
  const geocodeDebounceRef = useRef<number | undefined>(undefined);

  const [selected, setSelected] = useState<PickedLocation>(() => ({
    lat: initialValue?.lat ?? initialCenter.lat,
    lng: initialValue?.lng ?? initialCenter.lng,
    address: initialValue?.address
  }));
  const [addressStatus, setAddressStatus] = useState('Move the map to set location.');
  const [locationError, setLocationError] = useState('');

  const addressLabel = useMemo(() => {
    const uppercase = title.toUpperCase();
    return uppercase.includes('PICKUP') ? 'PICKUP ADDRESS' : 'DESTINATION ADDRESS';
  }, [title]);

  const reverseGeocode = async (lat: number, lng: number) => {
    if (!MAPBOX_TOKEN) {
      setAddressStatus('Map token missing. Set VITE_MAPBOX_TOKEN.');
      return;
    }

    try {
      setAddressStatus('Fetching address...');
      const response = await fetch(
        `https://api.mapbox.com/search/geocode/v6/reverse?longitude=${lng}&latitude=${lat}&access_token=${MAPBOX_TOKEN}`
      );

      if (!response.ok) {
        throw new Error(`Reverse geocoding failed (${response.status})`);
      }

      const data = await response.json();
      const feature = data?.features?.[0];
      const bestAddress = feature?.properties?.full_address || feature?.place_formatted || feature?.name;

      setSelected((prev) => ({ ...prev, address: bestAddress || undefined }));
      setAddressStatus(bestAddress || 'Unable to fetch address');
    } catch (error) {
      console.error(error);
      setSelected((prev) => ({ ...prev, address: undefined }));
      setAddressStatus('Unable to fetch address');
    }
  };

  useEffect(() => {
    if (!mapContainerRef.current || mapRef.current) {
      return;
    }

    if (!MAPBOX_TOKEN) {
      setAddressStatus('Map token missing. Set VITE_MAPBOX_TOKEN.');
      return;
    }

    mapboxgl.accessToken = MAPBOX_TOKEN;

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [selected.lng, selected.lat],
      zoom: 15
    });

    mapRef.current = map;

    const handleMoveEnd = () => {
      const center = map.getCenter();
      setSelected((prev) => ({ ...prev, lat: center.lat, lng: center.lng }));

      if (geocodeDebounceRef.current) {
        window.clearTimeout(geocodeDebounceRef.current);
      }

      geocodeDebounceRef.current = window.setTimeout(() => {
        reverseGeocode(center.lat, center.lng);
      }, 300);
    };

    map.on('load', () => {
      reverseGeocode(selected.lat, selected.lng);
    });
    map.on('moveend', handleMoveEnd);

    return () => {
      if (geocodeDebounceRef.current) {
        window.clearTimeout(geocodeDebounceRef.current);
      }
      map.off('moveend', handleMoveEnd);
      map.remove();
      mapRef.current = null;
    };
  }, []);

  const useCurrentLocation = () => {
    if (!navigator.geolocation) {
      setLocationError('Geolocation is not supported in this browser.');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        setLocationError('');
        const next = { lat: coords.latitude, lng: coords.longitude };
        setSelected((prev) => ({ ...prev, ...next }));
        mapRef.current?.easeTo({ center: [next.lng, next.lat], zoom: 16 });
        reverseGeocode(next.lat, next.lng);
      },
      () => {
        setLocationError('Unable to access your location. Check browser permissions and try again.');
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  };

  const confirmSelection = () => {
    onConfirm({ lat: selected.lat, lng: selected.lng, address: selected.address || undefined });
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/80">
      <div className="relative h-full w-full bg-white">
        <div ref={mapContainerRef} className="h-full w-full" />

        <button
          type="button"
          onClick={onCancel}
          className="absolute left-4 top-4 rounded-full bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow"
        >
          Back
        </button>

        <div className="pointer-events-none absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-full text-3xl drop-shadow">
          📍
        </div>

        <div className="absolute bottom-0 left-0 right-0 space-y-3 rounded-t-2xl bg-white p-4 shadow-[0_-4px_16px_rgba(0,0,0,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{addressLabel}</p>
          <p className="text-sm font-medium text-slate-800">{selected.address || addressStatus}</p>
          <p className="text-xs text-slate-500">
            {selected.lat.toFixed(6)}, {selected.lng.toFixed(6)}
          </p>

          {locationError && <p className="text-xs text-amber-600">{locationError}</p>}

          <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
            <button
              type="button"
              onClick={useCurrentLocation}
              className="rounded border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
            >
              Use my current location
            </button>
            <button
              type="button"
              onClick={confirmSelection}
              className="rounded bg-blue-600 px-4 py-2 text-sm font-semibold text-white"
            >
              Done
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
