interface BadgeEstadoProps {
  estado: string;
}

export function BadgeEstado({ estado }: BadgeEstadoProps) {
  const variantes: Record<string, string> = {
    EN_ESPERA: 'bg-amber-100 text-amber-800',
    LLAMADO: 'bg-blue-100 text-blue-800',
    EN_ATENCION: 'bg-indigo-100 text-indigo-800',
    EN_PAUSA: 'bg-slate-100 text-slate-800',
    FINALIZADO: 'bg-green-100 text-green-800',
    CANCELADO: 'bg-slate-200 text-slate-600',
    NO_ATENDIDO: 'bg-red-100 text-red-800',
  };

  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${variantes[estado] || 'bg-slate-100 text-slate-600'}`}>
      {estado.replace(/_/g, ' ')}
    </span>
  );
}
