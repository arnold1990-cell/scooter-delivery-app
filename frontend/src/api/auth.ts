import http from './http';
import type { AuthUser, UserRole } from '../types';

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

const toAuthUser = (data: AuthApiResponse): AuthUser => ({
  userId: data.userId,
  email: data.email,
  fullName: data.fullName,
  roles: data.roles,
  accessToken: data.accessToken || data.token || '',
  refreshToken: data.refreshToken
});

const authApi = {
  async login(payload: LoginPayload): Promise<AuthUser> {
    const { data } = await http.post<AuthApiResponse>('/auth/login', payload);
    return toAuthUser(data);
  },
  async register(payload: RegisterPayload): Promise<AuthUser> {
    const { data } = await http.post<AuthApiResponse>('/auth/register', payload);
    return toAuthUser(data);
  },
  async me(): Promise<MeResponse> {
    const { data } = await http.get<MeResponse>('/auth/me');
    return data;
  }
};

export default authApi;
