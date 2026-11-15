import { Link, useNavigate } from 'react-router-dom';
import { Building2 } from 'lucide-react';
import { useAuthContext } from '@/context/AuthContext';
import { UserMenu } from '@/components/UserMenu';
import { getHomeByRole } from '@/utils/roleRoutes';

// Encabezado público de la landing:
// - Sin sesión: Iniciar sesión + Crear cuenta.
// - Con sesión: Ir a mi panel + menú de usuario.
// - Hidratando sesión: reserva de espacio (sin salto de layout).
export function PublicNav() {
  const { user, loading, isAuthenticated } = useAuthContext();
  const navigate = useNavigate();

  return (
    <header className="sticky top-0 z-sticky border-b border-slate-200 bg-white/90 backdrop-blur">
      <div className="page-gutter mx-auto flex h-16 max-w-7xl items-center justify-between gap-2">
        <button
          className="flex items-center gap-2 rounded-md py-1 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600"
          onClick={() => navigate('/')}
          aria-label="Agendaty — inicio"
        >
          <Building2 className="h-6 w-6 text-brand-600" aria-hidden="true" />
          <span className="text-lg font-bold tracking-tight text-slate-900">Agendaty</span>
        </button>

        <nav className="flex items-center gap-2 sm:gap-3" aria-label="Navegación pública">
          {loading ? (
            <div className="h-11 w-36 rounded-md bg-slate-200/70" aria-hidden="true" />
          ) : isAuthenticated ? (
            <>
              <Link
                to={getHomeByRole(user?.rol)}
                className="inline-flex h-11 items-center rounded-md bg-brand-600 px-4 text-sm font-semibold text-white transition-colors hover:bg-brand-700"
              >
                Ir a mi panel
              </Link>
              <UserMenu />
            </>
          ) : (
            <>
              <Link to="/login" className="rounded-md px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">
                Iniciar sesión
              </Link>
              <Link
                to="/registro"
                className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700"
              >
                Crear cuenta
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
