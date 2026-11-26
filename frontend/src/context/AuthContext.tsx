import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { toast } from 'sonner';
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

// Lectura síncrona y tolerante a datos corruptos (una sola fuente de verdad)
const leerSesionInicial = (): AuthUser | null => {
  const storedUser = authService.getUser();
  const token = authService.getToken();
  return storedUser && token ? storedUser : null;
};

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(leerSesionInicial);
  const [loading] = useState(false);

  // Sincroniza la sesión entre pestañas del mismo origen:
  // 'storage' cubre escrituras desde otra pestaña, 'focus' cubre re-leer al volver a la pestaña.
  useEffect(() => {
    const refrescar = () => setUser(leerSesionInicial());
    const onKey = (key: string | null) => {
      if (key === null || key === 'user' || key === 'token') refrescar();
    };
    const onStorage = (e: StorageEvent) => onKey(e.key);
    const onFocus = () => refrescar();
    window.addEventListener('storage', onStorage);
    window.addEventListener('focus', onFocus);
    return () => {
      window.removeEventListener('storage', onStorage);
      window.removeEventListener('focus', onFocus);
    };
  }, []);

  const login = useCallback((userData: AuthUser) => {
    authService.setToken(userData.token);
    authService.setUser(userData);
    setUser(userData);
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
    toast.success('Sesión cerrada');
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
