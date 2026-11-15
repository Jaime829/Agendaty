import { createContext, useContext, useState, useCallback } from 'react';
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
  // La sesión se restaura de forma síncrona desde localStorage (sin parpadeos ni efecto extra)
  const [user, setUser] = useState<AuthUser | null>(() => {
    const storedUser = authService.getUser();
    const token = authService.getToken();
    return storedUser && token ? storedUser : null;
  });
  const [loading] = useState(false);

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
