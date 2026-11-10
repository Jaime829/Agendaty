import { useState, useEffect, useCallback } from 'react';
import { authService } from '@/api/auth';
import type { AuthUser } from '@/types';

export function useAuth() {
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

  const login = useCallback(
    async (token: string, userData: Omit<AuthUser, 'token' | 'tipo'>) => {
      authService.setToken(token);
      authService.setUser(userData);
      setUser({ token, tipo: 'Bearer', ...userData });
    },
    []
  );

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const isAuthenticated = !!user;
  const tieneRol = (rol: string) => user?.rol === rol;
  const tieneAlgunaRol = (roles: string[]) => roles.includes(user?.rol ?? '');

  return {
    user,
    loading,
    isAuthenticated,
    tieneRol,
    tieneAlgunaRol,
    login,
    logout,
  };
}
