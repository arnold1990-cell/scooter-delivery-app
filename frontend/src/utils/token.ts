import type { AuthUser } from '../types';

const KEY = 'scooter_access_token';
const USER_KEY = 'scooter_user';
const REFRESH_KEY = 'scooter_refresh_token';

export const getToken = () => localStorage.getItem(KEY);
export const setToken = (t: string) => localStorage.setItem(KEY, t);
export const getRefreshToken = () => localStorage.getItem(REFRESH_KEY);
export const setRefreshToken = (token?: string) => {
  if (!token) {
    localStorage.removeItem(REFRESH_KEY);
    return;
  }
  localStorage.setItem(REFRESH_KEY, token);
};
export const removeToken = () => {
  localStorage.removeItem(KEY);
  localStorage.removeItem(USER_KEY);
  localStorage.removeItem(REFRESH_KEY);
};
export const getUser = (): AuthUser | null => {
  const s = localStorage.getItem(USER_KEY);
  return s ? JSON.parse(s) : null;
};
export const setUser = (u: AuthUser) => localStorage.setItem(USER_KEY, JSON.stringify(u));
