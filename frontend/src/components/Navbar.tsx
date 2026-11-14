import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/context/AuthContext';
import { LogOut, User, Building2, Clock, Users, LayoutDashboard } from 'lucide-react';
import { Button } from '@/components/Button';

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
  { path: '/recepcionista', label: 'Recepcionista', rol: 'ROLE_RECEPCIONISTA' },
  { path: '/asesor', label: 'Asesor', rol: 'ROLE_ASESOR' },
  { path: '/coordinador', label: 'Coordinador', rol: 'ROLE_COORDINADOR' },
  { path: '/administrador', label: 'Administrador', rol: 'ROLE_ADMINISTRADOR' },
  { path: '/ciudadano', label: 'Ciudadano', rol: 'ROLE_CIUDADANO' },
];

export function Navbar() {
  const { user, logout, tieneAlgunaRol } = useAuthContext();
  const navigate = useNavigate();

  if (!user) return null;

  const misItems = items.filter(
    (i) => i.rol === user.rol || tieneAlgunaRol([i.rol, 'ROLE_ADMINISTRADOR'])
  );

  const rolLabel = rolLabels[user.rol] || user.rol;

  return (
    <nav className="bg-white border-b border-slate-200 sticky top-0 z-20">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3">
            <Building2 className="h-6 w-6 text-blue-600" />
            <span className="font-semibold text-slate-900">Agendaty</span>
          </div>
          <div className="hidden md:flex items-center gap-6">
            {misItems.map((i) => {
              const Icon = iconMap[i.rol] || Building2;
              return (
                <button
                  key={i.path}
                  onClick={() => navigate(i.path)}
                  className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-blue-600 rounded-md transition-colors"
                >
                  <Icon className="h-4 w-4" /> {i.label}
                </button>
              );
            })}
          </div>
          <div className="flex items-center gap-4">
            <span className="text-sm text-slate-600 hidden md:block">
              {user.username}{' '}
              <span className="text-xs bg-slate-100 text-slate-500 px-2 py-0.5 rounded ml-2">
                {rolLabel}
              </span>
            </span>
            <Button variant="outline" size="sm" onClick={logout}>
              <LogOut className="h-4 w-4 mr-2" /> Salir
            </Button>
          </div>
        </div>
      </div>
    </nav>
  );
}
