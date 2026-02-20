import { getToken, removeToken } from '../utils/token';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

if (import.meta.env.DEV) {
  console.info(`[api] using base URL: ${API_BASE_URL}`);
}

export class HttpError extends Error {
  status: number;
  data: unknown;

  constructor(status: number, data: unknown, message = 'Request failed') {
    super(message);
    this.name = 'HttpError';
    this.status = status;
    this.data = data;
  }
}

const resolveApiUrl = (path: string) => `${API_BASE_URL}${path.startsWith('/') ? '' : '/'}${path}`;

const parseResponseBody = async (response: Response) => {
  const contentType = response.headers.get('content-type') ?? '';
  if (contentType.includes('application/json')) {
    return response.json();
  }

  const text = await response.text();
  return text.length > 0 ? text : null;
};

const request = async <T>(path: string, init?: RequestInit): Promise<{ data: T }> => {
  const token = getToken();
  const headers = new Headers(init?.headers ?? {});

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  if (init?.body && !(init.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(resolveApiUrl(path), {
    ...init,
    headers,
    credentials: 'omit'
  });

  const data = await parseResponseBody(response);

  if (!response.ok) {
    if (response.status === 401) {
      removeToken();
      window.location.href = '/login';
    }

    throw new HttpError(response.status, data);
  }

  return { data: data as T };
};

const http = {
  get: <T>(path: string, init?: RequestInit) => request<T>(path, { ...init, method: 'GET' }),
  post: <T>(path: string, body?: unknown, init?: RequestInit) =>
    request<T>(path, {
      ...init,
      method: 'POST',
      body: body === undefined ? undefined : JSON.stringify(body)
    }),
  patch: <T>(path: string, body?: unknown, init?: RequestInit) =>
    request<T>(path, {
      ...init,
      method: 'PATCH',
      body: body === undefined ? undefined : JSON.stringify(body)
    })
};

export const isHttpError = (error: unknown): error is HttpError => error instanceof HttpError;

export default http;
