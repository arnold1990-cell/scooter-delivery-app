import { useEffect, useMemo, useRef, useState } from 'react';
import mapboxgl, { loadMapboxGl } from 'mapbox-gl';

export type PickedLocation = { lat: number; lng: number; address?: string };

type MapPickerProps = {
  title: string;
  initialCenter?: { lat: number; lng: number };
  initialValue?: PickedLocation | null;
  onConfirm: (value: PickedLocation) => void;
  onCancel: () => void;
  onLocationChange?: (value: PickedLocation) => void;
};

const DEFAULT_CENTER = { lat: -24.606264, lng: 25.944637 };
const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_TOKEN as string | undefined;

const getGeolocationErrorMessage = (error: GeolocationPositionError) => {
  switch (error.code) {
    case error.PERMISSION_DENIED:
      return 'Location access is blocked. Please allow location permissions or access this app over HTTPS.';
    case error.TIMEOUT:
      return 'Timed out while fetching your location. Try again in an open area or with better signal.';
    case error.POSITION_UNAVAILABLE:
      return 'Your current location is unavailable right now. Please try again in a moment.';
    default:
      return 'Unable to access your location. Check browser permissions and try again.';
  }
};

const getCurrentPosition = (options?: PositionOptions) =>
  new Promise<GeolocationPosition>((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation is not supported in this browser.'));
      return;
    }

    navigator.geolocation.getCurrentPosition(resolve, reject, options);
  });

const isInsecureContext = () => !window.isSecureContext && !import.meta.env.DEV;

export default function MapPicker({
  title,
  initialCenter = DEFAULT_CENTER,
  initialValue = null,
  onConfirm,
  onCancel,
  onLocationChange
}: MapPickerProps) {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<any>(null);
  const markerRef = useRef<any>(null);
  const geocodeDebounceRef = useRef<number | undefined>(undefined);

  const [selected, setSelected] = useState<PickedLocation>(() => ({
    lat: initialValue?.lat ?? initialCenter.lat,
    lng: initialValue?.lng ?? initialCenter.lng,
    address: initialValue?.address
  }));
  const [addressStatus, setAddressStatus] = useState('Drag the pin to set location.');
  const [locationError, setLocationError] = useState('');
  const [isLocating, setIsLocating] = useState(false);

  const addressLabel = useMemo(() => {
    const uppercase = title.toUpperCase();
    return uppercase.includes('PICKUP') ? 'PICKUP ADDRESS' : 'DROPOFF ADDRESS';
  }, [title]);

  useEffect(() => {
    if (!MAPBOX_TOKEN) {
      return;
    }

    mapboxgl.accessToken = MAPBOX_TOKEN;
  }, []);

  const reverseGeocode = async (lat: number, lng: number) => {
    if (!MAPBOX_TOKEN) {
      setAddressStatus('Map cannot load. Please configure VITE_MAPBOX_TOKEN.');
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

      setSelected((prev) => {
        const updated = { ...prev, lat, lng, address: bestAddress || undefined };
        onLocationChange?.(updated);
        return updated;
      });
      setAddressStatus(bestAddress || 'Unable to fetch address');
    } catch (error) {
      console.error(error);
      setSelected((prev) => {
        const updated = { ...prev, lat, lng, address: undefined };
        onLocationChange?.(updated);
        return updated;
      });
      setAddressStatus('Unable to fetch address');
    }
  };

  useEffect(() => {
    if (!mapContainerRef.current || mapRef.current || !MAPBOX_TOKEN) {
      return;
    }

    let isMounted = true;

    const setupMap = async () => {
      const mapboxInstance = await loadMapboxGl();
      if (!mapboxInstance || !isMounted || !mapContainerRef.current) {
        return;
      }

      mapboxgl.accessToken = MAPBOX_TOKEN;

      const map = new mapboxInstance.Map({
      container: mapContainerRef.current,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [selected.lng, selected.lat],
      zoom: 15
    });

      mapRef.current = map;

      const marker = new mapboxInstance.Marker({ draggable: true })
      .setLngLat([selected.lng, selected.lat])
      .addTo(map);

      markerRef.current = marker;

      const handleDragEnd = () => {
      const lngLat = marker.getLngLat();
      const lat = lngLat.lat;
      const lng = lngLat.lng;

      setSelected((prev) => {
        const updated = { ...prev, lat, lng };
        onLocationChange?.(updated);
        return updated;
      });

      if (geocodeDebounceRef.current) {
        window.clearTimeout(geocodeDebounceRef.current);
      }

      geocodeDebounceRef.current = window.setTimeout(() => {
        reverseGeocode(lat, lng);
      }, 250);
    };

      marker.on('dragend', handleDragEnd);
      map.on('load', () => {
        reverseGeocode(selected.lat, selected.lng);
      });

      return () => {
        marker.off('dragend', handleDragEnd);
        marker.remove();
        map.remove();
        mapRef.current = null;
        markerRef.current = null;
      };
    };

    let cleanup: (() => void) | undefined;

    setupMap().then((teardown) => {
      cleanup = teardown;
    });

    return () => {
      isMounted = false;
      cleanup?.();

      if (geocodeDebounceRef.current) {
        window.clearTimeout(geocodeDebounceRef.current);
      }
    };
  }, []);

  const useCurrentLocation = async () => {
    setLocationError('');
    setIsLocating(true);

    if (isInsecureContext()) {
      setLocationError('Location access is blocked. Please allow location permissions or access this app over HTTPS.');
      setIsLocating(false);
      return;
    }

    try {
      const { coords } = await getCurrentPosition({ enableHighAccuracy: true, timeout: 12000, maximumAge: 0 });
      const lat = coords.latitude;
      const lng = coords.longitude;

      setSelected((prev) => {
        const updated = { ...prev, lat, lng };
        onLocationChange?.(updated);
        return updated;
      });
      markerRef.current?.setLngLat([lng, lat]);
      mapRef.current?.flyTo({ center: [lng, lat], zoom: 16 });
      await reverseGeocode(lat, lng);
    } catch (error) {
      if (typeof error === 'object' && error !== null && 'code' in error) {
        const geolocationError = error as GeolocationPositionError;

        if (geolocationError.code === geolocationError.PERMISSION_DENIED || isInsecureContext()) {
          setLocationError(
            'Location access is blocked. Please allow location permissions or access this app over HTTPS.'
          );
          return;
        }

        setLocationError(getGeolocationErrorMessage(geolocationError));
        return;
      }

      setLocationError(
        isInsecureContext()
          ? 'Location access is blocked. Please allow location permissions or access this app over HTTPS.'
          : 'Unable to access your location. Check browser permissions and try again.'
      );
    } finally {
      setIsLocating(false);
    }
  };

  if (!MAPBOX_TOKEN) {
    return (
      <div className="fixed inset-0 z-50 bg-slate-900/80">
        <div className="relative flex h-full w-full items-center justify-center bg-white p-6">
          <div className="max-w-md space-y-4 text-center">
            <p className="text-lg font-semibold text-slate-900">Map cannot load. Please configure VITE_MAPBOX_TOKEN.</p>
            <button
              type="button"
              onClick={onCancel}
              className="rounded bg-slate-800 px-4 py-2 text-sm font-medium text-white"
            >
              Back
            </button>
          </div>
        </div>
      </div>
    );
  }

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
              disabled={isLocating}
              className="rounded border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
            >
              {isLocating ? 'Fetching current location...' : 'Use my current location'}
            </button>
            <button
              type="button"
              onClick={() => onConfirm(selected)}
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
