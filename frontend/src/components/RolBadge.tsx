const ROL_STYLE: Record<string, { clase: string; etiqueta: string }> = {
  ROLE_CIUDADANO: { clase: 'bg-green-50 text-green-800 ring-green-200', etiqueta: 'Ciudadano' },
  ROLE_ASESOR: { clase: 'bg-indigo-50 text-indigo-800 ring-indigo-200', etiqueta: 'Asesor' },
  ROLE_RECEPCIONISTA: { clase: 'bg-sky-50 text-sky-800 ring-sky-200', etiqueta: 'Recepcionista' },
  ROLE_COORDINADOR: { clase: 'bg-violet-50 text-violet-800 ring-violet-200', etiqueta: 'Coordinador' },
  ROLE_ADMINISTRADOR: { clase: 'bg-brand-50 text-brand-800 ring-brand-200', etiqueta: 'Administrador' },
};

export function RolBadge({ rol }: { rol: string }) {
  const style = ROL_STYLE[rol] ?? { clase: 'bg-slate-100 text-slate-700 ring-slate-200', etiqueta: rol };
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold leading-none ring-1 ring-inset ${style.clase}`}>
      {style.etiqueta}
    </span>
  );
}

export { ROL_STYLE };
