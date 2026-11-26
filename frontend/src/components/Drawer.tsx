import { ReactNode, useEffect, useRef } from 'react';

interface DrawerProps {
  titulo: string;
  descripcion?: string;
  onClose: () => void;
  children: ReactNode;
  pie?: ReactNode;
}

export function Drawer({ titulo, descripcion, onClose, children, pie }: DrawerProps) {
  const panelRef = useRef<HTMLDivElement>(null);
  const closeRef = useRef(onClose);

  // Mantener referencia fresca del callback sin re-registrar el listener de teclado
  useEffect(() => {
    closeRef.current = onClose;
  }, [onClose]);

  // Ejecutar solo al montar: la referencia onClose cambia en cada render del padre
  useEffect(() => {
    panelRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') closeRef.current();
    };
    document.addEventListener('keydown', onKey);
    const previo = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.style.overflow = previo;
    };
  }, []);

  return (
    <div className="fixed inset-0 z-modal flex justify-end" role="presentation">
      <div className="absolute inset-0 bg-slate-900/50 animate-fade-in" onClick={onClose} aria-hidden="true" />
      <div
        ref={panelRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="drawer-titulo"
        tabIndex={-1}
        className="relative z-10 flex h-full w-full max-w-lg flex-col overflow-y-auto bg-white shadow-pop animate-rise-in focus:outline-none"
      >
        <div className="sticky top-0 z-10 flex items-start justify-between gap-4 border-b border-slate-200 bg-white/95 px-6 py-4 backdrop-blur">
          <div>
            <h2 id="drawer-titulo" className="text-base font-semibold text-slate-900">
              {titulo}
            </h2>
            {descripcion && <p className="mt-0.5 text-sm text-slate-500">{descripcion}</p>}
          </div>
          <button
            onClick={onClose}
            aria-label="Cerrar panel"
            className="rounded-md px-2 text-2xl leading-none text-slate-400 hover:text-slate-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600"
          >
            ×
          </button>
        </div>
        <div className="flex-1 px-6 py-5">{children}</div>
        {pie && <div className="sticky bottom-0 z-10 flex justify-end gap-3 border-t border-slate-200 bg-white/95 px-6 py-4 backdrop-blur">{pie}</div>}
      </div>
    </div>
  );
}
