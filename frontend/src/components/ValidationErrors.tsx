interface ValidationErrorsProps {
  fieldErrors: Record<string, string>;
  onCerrar?: () => void;
}

export function ValidationErrors({ fieldErrors, onCerrar }: ValidationErrorsProps) {
  const errors = Object.entries(fieldErrors);

  return (
    <div className="mb-4 rounded-md border border-red-200 bg-red-50 p-4">
      <div className="flex items-start gap-3 mb-3">
        <span className="flex-shrink-0 mt-0.5">⚠</span>
        <p className="text-sm font-semibold text-red-800">Errores de validación:</p>
      </div>
      <ul className="list-disc list-inside text-sm text-red-700 space-y-1">
        {errors.map(([field, message]) => (
          <li key={field}>
            <span className="font-medium capitalize">{field}</span>: {message}
          </li>
        ))}
      </ul>
      {onCerrar && (
        <button
          onClick={onCerrar}
          className="mt-3 text-xs text-red-600 hover:underline"
        >
          Cerrar
        </button>
      )}
    </div>
  );
}
