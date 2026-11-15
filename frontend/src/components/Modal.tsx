import { ReactNode, useEffect, useRef } from 'react';

interface ModalProps {
  titulo: string;
  onClose: () => void;
  children: ReactNode;
  pie?: ReactNode;
}

export function Modal({ titulo, onClose, children, pie }: ModalProps) {
  const panelRef = useRef<HTMLDivElement>(null);
  const cierreRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    cierreRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    document.addEventListener('keydown', onKey);
    const previo = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.style.overflow = previo;
    };
  }, [onClose]);

  return (
    <div className="fixed inset-0 z-modal flex items-end justify-center sm:items-center" role="presentation">
      <div className="absolute inset-0 bg-slate-900/50 animate-fade-in" onClick={onClose} aria-hidden="true" />
      <div
        ref={panelRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-titulo"
        className="relative z-10 w-full max-w-md rounded-t-xl sm:rounded-xl bg-white p-6 shadow-pop animate-rise-in"
      >
        <div className="flex items-start justify-between gap-4">
          <h2 id="modal-titulo" className="text-base font-semibold text-slate-900">
            {titulo}
          </h2>
          <button
            ref={cierreRef}
            onClick={onClose}
            aria-label="Cerrar"
            className="rounded px-2 text-xl leading-none text-slate-400 hover:text-slate-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-600"
          >
            ×
          </button>
        </div>
        <div className="mt-4">{children}</div>
        {pie && <div className="mt-6 flex justify-end gap-3">{pie}</div>}
      </div>
    </div>
  );
}
