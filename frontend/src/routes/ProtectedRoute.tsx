import { Navigate, useLocation } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  roles?: string[];
}

export function ProtectedRoute({ children, roles }: ProtectedRouteProps) {
  const { user, loading, isAuthenticated, tieneRol, tieneAlgunaRol } = useAuthContext();
  const location = useLocation();

  if (loading) return null;

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !tieneAlgunaRol(roles)) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
