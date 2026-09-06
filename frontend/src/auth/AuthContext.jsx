import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import api from '../api/client';

const STORAGE_KEY = 'fastpass_session';
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? JSON.parse(stored) : null;
  });

  const login = useCallback(async (credentials) => {
    const { data } = await api.post('/api/v1/auth/login', credentials);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    setSession(data);
    return data;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setSession(null);
  }, []);

  useEffect(() => {
    const unauthorized = () => logout();
    window.addEventListener('fastpass:unauthorized', unauthorized);
    return () => window.removeEventListener('fastpass:unauthorized', unauthorized);
  }, [logout]);

  const value = useMemo(() => ({ session, login, logout, isAuthenticated: Boolean(session?.accessToken) }),
    [session, login, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
