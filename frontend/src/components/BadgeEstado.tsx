interface BadgeEstadoProps {
  estado: string;
}

interface EstadoStyle {
  clase: string;
  dot: string;
  etiqueta: string;
}

const variantes: Record<string, EstadoStyle> = {
  EN_ESPERA: { clase: 'bg-estado-espera-bg text-estado-espera-text', dot: 'bg-estado-espera-dot', etiqueta: 'En espera' },
  LLAMADO: { clase: 'bg-estado-llamado-bg text-estado-llamado-text', dot: 'bg-estado-llamado-dot', etiqueta: 'Llamado' },
  EN_ATENCION: { clase: 'bg-estado-atencion-bg text-estado-atencion-text', dot: 'bg-estado-atencion-dot', etiqueta: 'En atención' },
  EN_PAUSA: { clase: 'bg-estado-pausa-bg text-estado-pausa-text', dot: 'bg-estado-pausa-dot', etiqueta: 'En pausa' },
  FINALIZADO: { clase: 'bg-estado-finalizado-bg text-estado-finalizado-text', dot: 'bg-estado-finalizado-dot', etiqueta: 'Finalizado' },
  CANCELADO: { clase: 'bg-estado-cancelado-bg text-estado-cancelado-text', dot: 'bg-estado-cancelado-dot', etiqueta: 'Cancelado' },
  NO_ATENDIDO: { clase: 'bg-estado-noatendido-bg text-estado-noatendido-text', dot: 'bg-estado-noatendido-dot', etiqueta: 'No atendido' },
};

const fallback: EstadoStyle = { clase: 'bg-slate-100 text-slate-600', dot: 'bg-slate-400', etiqueta: '' };

export function BadgeEstado({ estado }: BadgeEstadoProps) {
  const v = variantes[estado] ?? { ...fallback, etiqueta: estado };
  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-semibold leading-none ${v.clase}`}
      aria-label={`Estado: ${v.etiqueta}`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${v.dot}`} aria-hidden="true" />
      {v.etiqueta}
    </span>
  );
}

export function BadgePrioridad({ prioritario }: { prioritario: boolean }) {
  if (!prioritario) {
    return <span className="text-xs text-slate-400">Normal</span>;
  }
  return (
    <span className="inline-flex items-center gap-1.5 rounded-full bg-amber-50 px-2.5 py-1 text-xs font-semibold leading-none text-amber-800 ring-1 ring-inset ring-amber-200">
      Prioritario
    </span>
  );
}
