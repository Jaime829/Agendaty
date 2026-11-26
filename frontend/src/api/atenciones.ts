import api from './client';
import type { Atencion } from '@/types';

export interface IniciarAtencionParams {
  turnoId: number;
  asesorId: number;
}

export interface FinalizarAtencionParams {
  turnoId: number;
  asesorId: number;
  resultado: string;
  observaciones?: string;
}

export const atencionesService = {
  iniciar: (params: IniciarAtencionParams): Promise<Atencion> =>
    api
      .put(`/atenciones/iniciar?turnoId=${params.turnoId}&asesorId=${params.asesorId}`)
      .then((r) => r.data),
  pausar: (turnoId: number, asesorId: number): Promise<Atencion> =>
    api.put(`/atenciones/${turnoId}/pausar?asesorId=${asesorId}`).then((r) => r.data),
  reanudar: (turnoId: number, asesorId: number): Promise<Atencion> =>
    api.put(`/atenciones/${turnoId}/reanudar?asesorId=${asesorId}`).then((r) => r.data),
  finalizar: (params: FinalizarAtencionParams): Promise<Atencion> =>
    api
      .put(
        `/atenciones/${params.turnoId}/finalizar?asesorId=${params.asesorId}`,
        {
          resultado: params.resultado,
          observaciones: params.observaciones,
        }
      )
      .then((r) => r.data),
  obtenerPorTurno: (turnoId: number): Promise<Atencion> =>
    api.get(`/atenciones/turno/${turnoId}`).then((r) => r.data),
};
