interface EmptyStateProps {
  titulo: string;
  descripcion?: string;
  accion?: React.ReactNode;
}

export function EmptyState({ titulo, descripcion, accion }: EmptyStateProps) {
  return (
    <div className="text-center py-12">
      <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-slate-100">
        <svg className="h-8 w-8 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="1"
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
          />
        </svg>
      </div>
      <h3 className="mt-8 text-sm font-semibold text-slate-900">{titulo}</h3>
      {descripcion && (
        <p className="mt-4 text-sm text-slate-500">{descripcion}</p>
      )}
      {accion && <div className="mt-8">{accion}</div>}
    </div>
  );
}
