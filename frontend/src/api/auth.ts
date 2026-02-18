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

const authApi = {
  async login(payload: LoginPayload): Promise<AuthUser> {
    const { data } = await http.post('/api/auth/login', payload);
    return data;
  },
  async register(payload: RegisterPayload): Promise<AuthUser> {
    const { data } = await http.post('/api/auth/register', payload);
    return data;
  },
  async me(): Promise<Omit<AuthUser, 'token'>> {
    const { data } = await http.get('/api/users/me');
    return data;
  }
};

export default authApi;
