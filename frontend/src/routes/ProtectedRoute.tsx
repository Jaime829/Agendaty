import { Navigate, useLocation } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';
import { getRouteByRol } from '@/utils/roleRoutes';

interface ProtectedRouteProps {
  children: React.ReactNode;
  roles?: string[];
}

export function ProtectedRoute({ children, roles }: ProtectedRouteProps) {
  const { user, loading, isAuthenticated, tieneAlgunaRol } = useAuthContext();
  const location = useLocation();
  console.log([user, loading, isAuthenticated]);

  if (loading) return null;

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !tieneAlgunaRol(roles)) {
    // Redirigir al dashboard correspondiente del usuario en lugar de a la landing
    return <Navigate to={getRouteByRol(user?.rol)} replace />;
  }

  return <>{children}</>;
}
