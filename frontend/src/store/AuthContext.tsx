import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import authApi, { type LoginPayload, type RegisterPayload } from '../api/auth';
import { getToken, getUser, removeToken, setToken, setUser } from '../utils/token';
import type { AuthUser } from '../types';

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  loading: boolean;
  login: (payload: LoginPayload) => Promise<AuthUser>;
  register: (payload: RegisterPayload) => Promise<AuthUser>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUserState] = useState<AuthUser | null>(null);
  const [token, setTokenState] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = getToken();
    const storedUser = getUser();
    if (storedToken && storedUser) {
      setTokenState(storedToken);
      setUserState(storedUser);
    }
    setLoading(false);
  }, []);

  const login = async (payload: LoginPayload) => {
    const data = await authApi.login(payload);
    setToken(data.token);
    setUser(data);
    setTokenState(data.token);
    setUserState(data);
    return data;
  };

  const register = async (payload: RegisterPayload) => {
    const data = await authApi.register(payload);
    setToken(data.token);
    setUser(data);
    setTokenState(data.token);
    setUserState(data);
    return data;
  };

  const logout = () => {
    removeToken();
    setTokenState(null);
    setUserState(null);
  };

  const value = useMemo(() => ({ user, token, loading, login, register, logout }), [user, token, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
