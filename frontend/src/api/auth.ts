import { api } from './client';
import { normalizeAuthorities, normalizeRoles } from '../constants/roles';
import type { AuthUser, Authority, UserRole } from '../types';

const decodeTokenPayload = (token: string): Record<string, unknown> | null => {
  try {
    const [, payload] = token.split('.');
    if (!payload) return null;
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
    return JSON.parse(atob(padded));
  } catch {
    return null;
  }
};

const extractAuthoritiesFromToken = (token: string): Authority[] => {
  const payload = decodeTokenPayload(token);
  if (!payload) return [];

  const tokenAuthorities = payload.authorities;
  if (!Array.isArray(tokenAuthorities)) return [];

  return normalizeAuthorities(tokenAuthorities.map((authority) => String(authority)));
};

export interface RegisterPayload {
  fullName: string;
  email: string;
  password: string;
  role: Exclude<UserRole, 'ADMIN'>;
}

export interface LoginPayload {
  email: string;
  password: string;
}

interface AuthApiResponse {
  userId: string;
  email: string;
  fullName: string;
  roles: UserRole[];
  accessToken: string;
  refreshToken?: string;
  token?: string;
}

interface MeResponse {
  userId: string;
  email: string;
  roles: UserRole[];
}

const toAuthUser = (data: AuthApiResponse): AuthUser => {
  const token = data.accessToken || data.token || '';
  const tokenAuthorities = extractAuthoritiesFromToken(token);
  const mergedRoles = normalizeRoles([...(data.roles ?? []), ...tokenAuthorities]);

  if (import.meta.env.DEV) {
    console.debug('[auth] login/register roles resolved', {
      responseRoles: data.roles,
      tokenAuthorities,
      mergedRoles
    });
  }

  return {
    userId: data.userId,
    email: data.email,
    fullName: data.fullName,
    roles: mergedRoles,
    authorities: tokenAuthorities,
    accessToken: token,
    refreshToken: data.refreshToken
  };
};

const authApi = {
  async login(payload: LoginPayload): Promise<AuthUser> {
    const { data } = await api.post<AuthApiResponse>('/auth/login', payload);
    return toAuthUser(data);
  },
  async register(payload: RegisterPayload): Promise<AuthUser> {
    const { data } = await api.post<AuthApiResponse>('/auth/register', payload);
    return toAuthUser(data);
  },
  async me(): Promise<MeResponse> {
    const { data } = await api.get<MeResponse>('/auth/me');
    return data;
  }
};

export default authApi;
