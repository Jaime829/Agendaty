import { ReactNode } from 'react';

interface KpiCardProps {
  etiqueta: string;
  valor: ReactNode;
  detalle?: ReactNode;
  destacado?: boolean;
}

export function KpiCard({ etiqueta, valor, detalle, destacado = false }: KpiCardProps) {
  return (
    <div
      className={`flex h-full flex-col justify-between rounded-xl border p-4 shadow-card ${
        destacado ? 'border-brand-200 bg-brand-50' : 'border-slate-200 bg-white'
      }`}
    >
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{etiqueta}</p>
      <p className="mt-1 text-3xl font-bold tabular-nums tracking-tight text-slate-900">{valor}</p>
      {detalle && <div className="mt-2 text-xs text-slate-500">{detalle}</div>}
    </div>
  );
}
