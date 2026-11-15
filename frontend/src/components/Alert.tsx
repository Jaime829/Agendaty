interface AlertProps {
  tipo: 'error' | 'success' | 'warning';
  mensaje: string;
  fieldErrors?: Record<string, string>;
  onCerrar?: () => void;
}

const estilos = {
  error: { caja: 'border-red-200 bg-red-50 text-red-800', icono: '⚠', rol: 'alert' as const },
  success: { caja: 'border-green-200 bg-green-50 text-green-800', icono: '✓', rol: 'status' as const },
  warning: { caja: 'border-amber-200 bg-amber-50 text-amber-800', icono: '!', rol: 'alert' as const },
};

export function Alert({ tipo, mensaje, fieldErrors, onCerrar }: AlertProps) {
  const { caja, icono, rol } = estilos[tipo];
  const errores = fieldErrors ? Object.entries(fieldErrors) : [];
  const hayErroresDeCampo = errores.length > 0;

  return (
    <div role={rol} className={`rounded-md border p-4 ${caja}`}>
      <div className="flex items-start gap-3">
        <span className="flex-shrink-0 mt-0.5" aria-hidden="true">
          {icono}
        </span>
        <div className="min-w-0 flex-1">
          {hayErroresDeCampo ? (
            <>
              <p className="text-sm font-semibold">Errores de validación:</p>
              <ul className="mt-2 list-disc space-y-1 pl-5 text-sm">
                {errores.map(([campo, detalle]) => (
                  <li key={campo}>
                    <span className="font-medium">{campo}</span>: {detalle}
                  </li>
                ))}
              </ul>
            </>
          ) : (
            <p className="text-sm">{mensaje}</p>
          )}
        </div>
        {onCerrar && (
          <button
            onClick={onCerrar}
            aria-label="Cerrar mensaje"
            className="ml-auto flex-shrink-0 rounded px-2 text-lg leading-none opacity-70 hover:opacity-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-current"
          >
            ×
          </button>
        )}
      </div>
    </div>
  );
}
