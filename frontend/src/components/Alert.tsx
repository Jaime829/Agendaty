interface AlertProps {
  tipo: 'error' | 'success' | 'warning';
  mensaje: string;
  onCerrar?: () => void;
}

export function Alert({ tipo, mensaje, onCerrar }: AlertProps) {
  const colores: Record<string, string> = {
    error: 'bg-red-50 border-red-200 text-red-800',
    success: 'bg-green-50 border-green-200 text-green-800',
    warning: 'bg-amber-50 border-amber-200 text-amber-800',
  };

  return (
    <div className={`mb-4 rounded-md border p-4 flex items-start gap-3 ${colores[tipo]}`}>
      <span className="flex-shrink-0 mt-0.5">{tipo === 'error' ? '⚠' : tipo === 'success' ? '✓' : '!'}</span>
      <p className="text-sm">{mensaje}</p>
      {onCerrar && (
        <button
          onClick={onCerrar}
          className="ml-auto mt-0.5 text-sm hover:underline"
        >
          Cerrar
        </button>
      )}
    </div>
  );
}
