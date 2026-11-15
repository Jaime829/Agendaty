import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';
import { getRouteByRol } from '@/utils/roleRoutes';

/**
 * Redirige al usuario si no está autenticado o no tiene uno de los roles permitidos.
 * Debe llamarse en la parte superior del componente, antes de cualquier hook condicional.
 */
export function useRolGuard(rolesPermitidos: string[]) {
  const { user, tieneAlgunaRol, loading } = useAuthContext();
  const navigate = useNavigate();

  useEffect(() => {
    if (loading) return;
    if (!user) {
      navigate('/login', { replace: true });
      return;
    }
    if (!tieneAlgunaRol(rolesPermitidos)) {
      navigate(getRouteByRol(user.rol), { replace: true });
    }
  }, [loading, user, tieneAlgunaRol, navigate, rolesPermitidos]);

  return { esPermitido: !!user && tieneAlgunaRol(rolesPermitidos), user, loading };
}
