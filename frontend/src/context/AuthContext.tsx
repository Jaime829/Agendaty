import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authService } from '@/api/auth';
import type { AuthUser } from '@/types';

const AuthContext = createContext<{
  user: AuthUser | null;
  loading: boolean;
  login: (userData: AuthUser) => void;
  logout: () => void;
  isAuthenticated: boolean;
  tieneRol: (rol: string) => boolean;
  tieneAlgunaRol: (roles: string[]) => boolean;
} | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const u = authService.getUser();
    const token = authService.getToken();
    if (u && token) {
      setUser(u);
    }
    setLoading(false);
  }, []);

  const login = useCallback((userData: AuthUser) => {
    authService.setToken(userData.token);
    authService.setUser(userData);
    setUser(userData);
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const isAuthenticated = !!user;
  const tieneRol = (rol: string) => user?.rol === rol;
  const tieneAlgunaRol = (roles: string[]) => roles.includes(user?.rol ?? '');

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated,
    tieneRol,
    tieneAlgunaRol,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuthContext = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuthContext debe usarse dentro de un AuthProvider');
  }
  return context;
};
