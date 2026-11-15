import { SelectHTMLAttributes, useId } from 'react';

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options?: { value: string; label: string }[];
  placeholder?: string;
}

export function Select({ label, error, options = [], placeholder = 'Seleccione…', className = '', id, ...props }: SelectProps) {
  const autoId = useId();
  const selectId = id ?? autoId;
  const errorId = error ? `${selectId}-error` : undefined;
  return (
    <div className="mb-4">
      {label && (
        <label htmlFor={selectId} className="mb-1 block text-sm font-medium text-slate-700">
          {label}
        </label>
      )}
      <select
        id={selectId}
        aria-invalid={error ? true : undefined}
        aria-describedby={errorId}
        className={`block h-11 w-full md:max-w-md rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 transition-shadow focus:outline-none focus:ring-2 focus:ring-brand-600 focus:border-brand-600 ${
          error ? 'border-red-500 focus:ring-red-500' : ''
        } ${className}`}
        {...props}
      >
        <option value="" disabled>
          {placeholder}
        </option>
        {options.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
      {error && (
        <p id={errorId} className="mt-1 text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}
