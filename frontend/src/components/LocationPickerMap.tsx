export type LocationValue = { lat: number; lng: number };

type LocationPickerMapProps = {
  label: string;
  value: LocationValue | null;
  onChange: (value: LocationValue | null) => void;
};

export default function LocationPickerMap({ label, value, onChange }: LocationPickerMapProps) {
  const handleLatChange = (lat: string) => {
    if (lat === '') {
      onChange(null);
      return;
    }

    const parsedLat = Number(lat);
    if (Number.isNaN(parsedLat)) {
      return;
    }

    onChange({ lat: parsedLat, lng: value?.lng ?? 0 });
  };

  const handleLngChange = (lng: string) => {
    if (lng === '') {
      onChange(null);
      return;
    }

    const parsedLng = Number(lng);
    if (Number.isNaN(parsedLng)) {
      return;
    }

    onChange({ lat: value?.lat ?? 0, lng: parsedLng });
  };

  return (
    <section className="space-y-2 rounded border border-slate-200 p-4">
      <p className="font-medium text-slate-700">{label}</p>

      <div className="grid gap-3 sm:grid-cols-2">
        <label className="text-sm text-slate-700">
          Latitude
          <input
            type="number"
            step="any"
            value={value?.lat ?? ''}
            onChange={(event) => handleLatChange(event.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            placeholder="-24.6282"
          />
        </label>

        <label className="text-sm text-slate-700">
          Longitude
          <input
            type="number"
            step="any"
            value={value?.lng ?? ''}
            onChange={(event) => handleLngChange(event.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            placeholder="25.9231"
          />
        </label>
      </div>

      <p className="text-sm text-slate-600">
        {value
          ? `Selected: ${value.lat.toFixed(6)}, ${value.lng.toFixed(6)}`
          : 'Enter latitude and longitude coordinates.'}
      </p>
    </section>
  );
}
