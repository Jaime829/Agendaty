import { useState, useEffect } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';
import { User, Building2, Clock, Users, LayoutDashboard, Menu, X, LogOut } from 'lucide-react';
import { UserMenu } from '@/components/UserMenu';

const iconMap: Record<string, React.ElementType> = {
  ROLE_RECEPCIONISTA: Building2,
  ROLE_ASESOR: User,
  ROLE_COORDINADOR: Users,
  ROLE_ADMINISTRADOR: LayoutDashboard,
  ROLE_CIUDADANO: Clock,
};

const rolLabels: Record<string, string> = {
  ROLE_CIUDADANO: 'Ciudadano',
  ROLE_RECEPCIONISTA: 'Recepcionista',
  ROLE_ASESOR: 'Asesor',
  ROLE_COORDINADOR: 'Coordinador',
  ROLE_ADMINISTRADOR: 'Administrador',
};

const items: { path: string; label: string; rol: string }[] = [
  { path: '/recepcionista', label: 'Recepción', rol: 'ROLE_RECEPCIONISTA' },
  { path: '/asesor', label: 'Asesor', rol: 'ROLE_ASESOR' },
  { path: '/coordinador', label: 'Coordinador', rol: 'ROLE_COORDINADOR' },
  { path: '/administrador', label: 'Administración', rol: 'ROLE_ADMINISTRADOR' },
  { path: '/ciudadano', label: 'Mi turno', rol: 'ROLE_CIUDADANO' },
  { path: '/reportes', label: 'Reportes', rol: 'ROLE_COORDINADOR' },
];

export function Navbar() {
  const { user, logout } = useAuthContext();
  const navigate = useNavigate();
  const location = useLocation();
  const [menuAbierto, setMenuAbierto] = useState(false);

  const esRutaPublica = ['/', '/login', '/registro'].includes(location.pathname);

  // Bloquea el scroll del body cuando el menú móvil está abierto
  useEffect(() => {
    document.body.style.overflow = menuAbierto ? 'hidden' : '';
    return () => {
      document.body.style.overflow = '';
    };
  }, [menuAbierto]);

  if (!user || esRutaPublica) return null;

  const esAdmin = user.rol === 'ROLE_ADMINISTRADOR';
  const misItems = items.filter((i) => i.rol === user.rol || esAdmin);
  const rolLabel = rolLabels[user.rol] || user.rol;
  const userLabel = user.username || 'Usuario';

  const enlace = (i: (typeof items)[number], grande = false) => {
    const Icon = iconMap[i.rol] || Building2;
    return (
      <NavLink
        key={i.path}
        to={i.path}
        aria-current="page"
        aria-label={i.label}
        onClick={() => setMenuAbierto(false)}
        className={({ isActive }) =>
          `flex focus-ring items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors ${
            isActive
              ? 'bg-brand-50 text-brand-800 ring-1 ring-inset ring-brand-200'
              : 'text-slate-600 hover:bg-slate-100 hover:text-brand-800'
          } ${grande ? 'h-12 text-base' : ''}`
        }
      >
        <Icon className="h-4 w-4" aria-hidden="true" /> {i.label}
      </NavLink>
    );
  };

  const cerrarSesion = () => {
    logout();
    navigate('/', { replace: true });
  };

  return (
    <nav className="sticky top-0 z-sticky border-b border-slate-200 bg-white/90 backdrop-blur">
      <div className="page-gutter mx-auto max-w-7xl">
        <div className="flex h-16 items-center justify-between gap-4">
          {/* Marca */}
          <button
            className="flex items-center gap-2 rounded-md py-1 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600"
            onClick={() => {
              setMenuAbierto(false);
              navigate('/');
            }}
            aria-label="Agendaty — inicio"
          >
            <Building2 className="h-6 w-6 text-brand-600" aria-hidden="true" />
            <span className="text-lg font-bold tracking-tight text-slate-900">Agendaty</span>
          </button>

          {/* Navegación escritorio */}
          <div className="hidden items-center gap-1 lg:flex" role="navigation" aria-label="Navegación principal">
            {misItems.map((i) => enlace(i))}
          </div>

          {/* Menú de usuario + hamburguesa móvil */}
          <div className="flex items-center gap-2">
            <UserMenu onLogout={cerrarSesion} />
            <button
              className="flex h-11 w-11 items-center justify-center rounded-md text-slate-700 hover:bg-slate-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600 lg:hidden"
              onClick={() => setMenuAbierto((v) => !v)}
              aria-expanded={menuAbierto}
              aria-controls="menu-movil"
              aria-label={menuAbierto ? 'Cerrar menú' : 'Abrir menú'}
            >
              {menuAbierto ? <X className="h-6 w-6" aria-hidden="true" /> : <Menu className="h-6 w-6" aria-hidden="true" />}
            </button>
          </div>
        </div>
      </div>

      {/* Menú móvil desplegable */}
      {menuAbierto && (
        <div id="menu-movil" className="border-t border-slate-200 bg-white shadow-pop lg:hidden">
          <div className="page-gutter mx-auto max-w-7xl py-4" role="navigation" aria-label="Menú móvil">
            <p className="mb-2 px-3 text-xs font-semibold uppercase tracking-wide text-slate-400">
              {userLabel} · {rolLabel}
            </p>
            <div className="flex flex-col gap-1">{misItems.map((i) => enlace(i, true))}</div>
            <div className="mt-3 border-t border-slate-100 pt-3">
              <button
                onClick={cerrarSesion}
                className="flex h-12 w-full items-center gap-2 rounded-md px-3 text-base font-medium text-slate-700 hover:bg-slate-100"
              >
                <LogOut className="h-5 w-5" aria-hidden="true" /> Cerrar sesión
              </button>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
}
