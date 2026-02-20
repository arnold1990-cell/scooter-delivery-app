import L, { type LatLngExpression } from 'leaflet';
import { MapContainer, Marker, TileLayer, useMapEvents } from 'react-leaflet';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';

export type LocationValue = { lat: number; lng: number };

type LocationPickerMapProps = {
  label: string;
  value: LocationValue | null;
  onChange: (value: LocationValue | null) => void;
  initialCenter?: [number, number];
};

L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow
});

function MapClickHandler({ onSelect }: { onSelect: (value: LocationValue) => void }) {
  useMapEvents({
    click(event) {
      onSelect({ lat: event.latlng.lat, lng: event.latlng.lng });
    }
  });

  return null;
}

export default function LocationPickerMap({
  label,
  value,
  onChange,
  initialCenter = [-24.6282, 25.9231]
}: LocationPickerMapProps) {
  const center: LatLngExpression = value ? [value.lat, value.lng] : initialCenter;

  return (
    <section className="space-y-2">
      <p className="font-medium text-slate-700">{label}</p>
      <MapContainer center={center} zoom={13} scrollWheelZoom className="h-64 w-full rounded border">
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <MapClickHandler onSelect={onChange} />
        {value && <Marker position={[value.lat, value.lng]} />}
      </MapContainer>

      <div className="text-sm text-slate-600">
        {value
          ? `Selected: ${value.lat.toFixed(6)}, ${value.lng.toFixed(6)}`
          : 'No location selected yet. Click on the map to choose one.'}
      </div>
    </section>
  );
}
