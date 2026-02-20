const MAPBOX_SCRIPT_URL = 'https://api.mapbox.com/mapbox-gl-js/v3.10.0/mapbox-gl.js';

type MapboxGlobal = typeof window & {
  mapboxgl?: any;
};

const getGlobalMapbox = () => (window as MapboxGlobal).mapboxgl;

export const loadMapboxGl = async () => {
  if (typeof window === 'undefined') {
    return null;
  }

  if (getGlobalMapbox()) {
    return getGlobalMapbox();
  }

  const existingScript = document.querySelector<HTMLScriptElement>('script[data-mapbox-gl="true"]');
  if (!existingScript) {
    const script = document.createElement('script');
    script.src = MAPBOX_SCRIPT_URL;
    script.async = true;
    script.dataset.mapboxGl = 'true';
    document.head.appendChild(script);

    await new Promise<void>((resolve, reject) => {
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Failed to load Mapbox script'));
    });
  } else if (!getGlobalMapbox()) {
    await new Promise<void>((resolve, reject) => {
      existingScript.addEventListener('load', () => resolve(), { once: true });
      existingScript.addEventListener('error', () => reject(new Error('Failed to load Mapbox script')), { once: true });
    });
  }

  return getGlobalMapbox() ?? null;
};

const mapboxProxy = {
  get accessToken() {
    return getGlobalMapbox()?.accessToken ?? '';
  },
  set accessToken(value: string) {
    const mapbox = getGlobalMapbox();
    if (mapbox) {
      mapbox.accessToken = value;
    }
  }
} as any;

export default mapboxProxy;
