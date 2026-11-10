import api from './client';

export interface FiltrosReporte {
  fechaDesde?: string;
  fechaHasta?: string;
  asesorId?: number;
  tipoTramiteId?: number;
}

export const reportesService = {
  tiempoEsperaPromedio: (f: FiltrosReporte = {}): Promise<any> =>
    api.get('/reportes/tiempo-espera-promedio', { params: f }).then((r) => r.data),
  turnosAtendidos: (f: FiltrosReporte = {}): Promise<any> =>
    api.get('/reportes/turnos-atendidos', { params: f }).then((r) => r.data),
  tramitesFrecuentes: (f: FiltrosReporte = {}): Promise<any[]> =>
    api.get('/reportes/tramites-frecuentes', { params: f }).then((r) => r.data),
};
