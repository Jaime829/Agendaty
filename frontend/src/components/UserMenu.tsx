import { useState, useRef, useEffect } from 'react';
import { ChevronDown, LogOut } from 'lucide-react';
import { useAuthContext } from '@/context/AuthContext';

const rolLabels: Record<string, string> = {
  ROLE_CIUDADANO: 'Ciudadano',
  ROLE_RECEPCIONISTA: 'Recepcionista',
  ROLE_ASESOR: 'Asesor',
  ROLE_COORDINADOR: 'Coordinador',
  ROLE_ADMINISTRADOR: 'Administrador',
};

type UserMenuProps = {
  onLogout?: () => void;
};

// Menú desplegable del usuario autenticado: iniciales, nombre, rol y Cerrar sesión.
// Idéntico en la navbar de la app y en el header público.
// onLogout permite al padre decidir qué hacer tras cerrar sesión (por defecto: ya navega con el contexto).
export function UserMenu({ onLogout }: UserMenuProps) {
  const { user, logout } = useAuthContext();
  const [abierto, setAbierto] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setAbierto(false);
      if (e.key === 'ArrowDown' && document.activeElement === ref.current?.querySelector('button')) {
        e.preventDefault();
        setAbierto(true);
        ref.current?.querySelector<HTMLButtonElement>('[role="menuitem"]')?.focus();
      }
    };
    const onClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setAbierto(false);
    };
    document.addEventListener('keydown', onKey);
    document.addEventListener('mousedown', onClick);
    return () => {
      document.removeEventListener('keydown', onKey);
      document.removeEventListener('mousedown', onClick);
    };
  }, []);

  if (!user) return null;

  const userLabel = user.username || 'Usuario';
  const iniciales = userLabel
    .split('@')[0]
    .split(/[.\s_-]/)
    .filter(Boolean)
    .slice(0, 2)
    .map((s: string) => s[0]?.toUpperCase() ?? '')
    .join('');
  const rolLabel = rolLabels[user.rol] || user.rol;

  return (
    <div className="relative" ref={ref}>
      <button
        onClick={() => setAbierto((v) => !v)}
        aria-expanded={abierto}
        aria-controls="menu-usuario"
        aria-haspopup="menu"
        aria-label="Abrir menú de usuario"
        className="flex h-11 items-center gap-2 rounded-md px-2 text-left transition-colors hover:bg-slate-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600"
      >
        <span className="flex h-8 w-8 items-center justify-center rounded-full bg-brand-600 text-xs font-bold text-white" aria-hidden="true">
          {iniciales}
        </span>
        <span className="hidden max-w-40 flex-col leading-tight md:flex">
          <span className="truncate text-sm font-semibold text-slate-800">{userLabel}</span>
          <span className="text-[11px] font-medium uppercase tracking-wide text-slate-500">{rolLabel}</span>
        </span>
        <ChevronDown className="hidden h-4 w-4 text-slate-400 md:block" aria-hidden="true" />
      </button>

      {abierto && (
        <div
          id="menu-usuario"
          role="menu"
          className="absolute right-0 top-12 z-dropdown w-64 overflow-hidden rounded-lg border border-slate-200 bg-white shadow-pop animate-fade-in"
        >
          <div className="border-b border-slate-100 px-4 py-3">
            <p className="truncate text-sm font-semibold text-slate-900">{userLabel}</p>
            <p className="mt-1 text-xs text-slate-500">{rolLabel}</p>
          </div>
          <button
            role="menuitem"
            onClick={() => (onLogout ? onLogout() : logout())}
            className="flex w-full items-center gap-2 px-4 py-3 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50 hover:text-slate-900 focus-visible:outline-none focus-visible:bg-slate-50"
          >
            <LogOut className="h-4 w-4" aria-hidden="true" /> Cerrar sesión
          </button>
        </div>
      )}
    </div>
  );
}
