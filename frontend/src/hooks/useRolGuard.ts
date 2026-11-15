import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';
import { getHomeByRole } from '@/utils/roleRoutes';

/**
 * Redirige al usuario si no está autenticado o no tiene uno de los roles permitidos.
 * `rolesPermitidos` se compara por contenido (unión), no por identidad del array.
 * Debe llamarse en la parte superior del componente, antes de cualquier hook condicional.
 */
export function useRolGuard(rolesPermitidos: string[]) {
  const { user, tieneAlgunaRol, loading } = useAuthContext();
  const navigate = useNavigate();
  const key = rolesPermitidos.join('|');

  useEffect(() => {
    if (loading) return;
    const roles = key.split('|');
    if (!user) {
      navigate('/login', { replace: true });
      return;
    }
    if (!tieneAlgunaRol(roles)) {
      navigate(getHomeByRole(user.rol), { replace: true });
    }
  }, [loading, user, tieneAlgunaRol, navigate, key]);

  return { esPermitido: !!user && tieneAlgunaRol(rolesPermitidos), user, loading };
}

