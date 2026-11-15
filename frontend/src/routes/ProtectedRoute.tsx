import { Navigate, useLocation } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';
import { getHomeByRole } from '@/utils/roleRoutes';
import { Loading } from '@/components/Loading';

interface ProtectedRouteProps {
  children: React.ReactNode;
  roles?: string[];
}

export function ProtectedRoute({ children, roles }: ProtectedRouteProps) {
  const { user, loading, isAuthenticated, tieneAlgunaRol } = useAuthContext();
  const location = useLocation();

  if (loading) return <Loading etiqueta="Cargando sesión…" />;

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !tieneAlgunaRol(roles)) {
    // Redirigir al dashboard correspondiente del usuario en lugar de a la landing
    return <Navigate to={getHomeByRole(user?.rol)} replace />;
  }

  return <>{children}</>;
}

