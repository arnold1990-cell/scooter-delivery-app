import type { AuthUser } from '../types';

const KEY = 'scooter_token';
const USER_KEY = 'scooter_user';
export const getToken = () => localStorage.getItem(KEY);
export const setToken = (t: string) => localStorage.setItem(KEY, t);
export const removeToken = () => {
  localStorage.removeItem(KEY);
  localStorage.removeItem(USER_KEY);
};
export const getUser = (): AuthUser | null => {
  const s = localStorage.getItem(USER_KEY);
  return s ? JSON.parse(s) : null;
};
export const setUser = (u: AuthUser) => localStorage.setItem(USER_KEY, JSON.stringify(u));
