import api from './client';
import type { Turno, TurnoRequest } from '@/types';

export interface GenerarTurnoParams {
  usuarioId: number;
  turno: TurnoRequest;
}

export const turnosService = {
  generar: (params: GenerarTurnoParams): Promise<Turno> =>
    api
      .post(`/turnos?usuarioId=${params.usuarioId}`, params.turno)
      .then((r) => r.data),
  listar: (params?: {
    estado?: string;
    prioridad?: string;
    ciudadanoId?: number;
    tipoTramiteId?: number;
  }): Promise<Turno[]> =>
    api.get('/turnos', { params }).then((r) => r.data),
  obtener: (id: number): Promise<Turno> =>
    api.get(`/turnos/${id}`).then((r) => r.data),
  llamarSiguiente: (asesorId: number): Promise<Turno> =>
    api.put(`/turnos/llamar-siguiente?asesorId=${asesorId}`).then((r) => r.data),
  cancelar: (id: number, usuarioId: number): Promise<Turno> =>
    api.put(`/turnos/${id}/cancelar?usuarioId=${usuarioId}`).then((r) => r.data),
  marcarNoAtendido: (id: number): Promise<Turno> =>
    api.put(`/turnos/${id}/no-atendido`).then((r) => r.data),
};
