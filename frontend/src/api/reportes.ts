import api from './client';

export interface FiltrosReporte {
  fechaDesde?: string;
  fechaHasta?: string;
  asesorId?: number;
  tipoTramiteId?: number;
}

// El backend declara fechaDesde/fechaHasta como LocalDateTime (ISO.DATE_TIME):
// "2026-11-09" es inválido; se envía "2026-11-09T00:00:00" / <hasta>T23:59:59.999
function toIsoDateTime(fecha: string | undefined, finDelDia: boolean): string | undefined {
  if (!fecha) return undefined;
  if (/^\d{4}-\d{2}-\d{2}$/.test(fecha)) {
    return finDelDia ? `${fecha}T23:59:59.999` : `${fecha}T00:00:00`;
  }
  return fecha; // ya viene en formato datetime
}

function buildParams(f: FiltrosReporte): Record<string, string | number> {
  const params: Record<string, string | number> = {};
  const desde = toIsoDateTime(f.fechaDesde, false);
  const hasta = toIsoDateTime(f.fechaHasta, true);
  if (desde) params.fechaDesde = desde;
  if (hasta) params.fechaHasta = hasta;
  if (f.asesorId != null) params.asesorId = f.asesorId;
  if (f.tipoTramiteId != null) params.tipoTramiteId = f.tipoTramiteId;
  return params;
}

export const reportesService = {
  tiempoEsperaPromedio: (f: FiltrosReporte = {}): Promise<any> =>
    api.get('/reportes/tiempo-espera-promedio', { params: buildParams(f) }).then((r) => r.data),
  turnosAtendidos: (f: FiltrosReporte = {}): Promise<any> =>
    api.get('/reportes/turnos-atendidos', { params: buildParams(f) }).then((r) => r.data),
  tramitesFrecuentes: (f: FiltrosReporte = {}): Promise<any[]> =>
    api.get('/reportes/tramites-frecuentes', { params: buildParams(f) }).then((r) => r.data),
};
