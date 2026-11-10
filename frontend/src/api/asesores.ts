import api from './client';
import type { Asesor } from '@/types';

export const asesoresService = {
  listar: (): Promise<Asesor[]> => api.get('/asesores').then((r) => r.data),
  obtener: (id: number): Promise<Asesor> => api.get(`/asesores/${id}`).then((r) => r.data),
  cambiarDisponibilidad: (id: number, disponible: boolean): Promise<Asesor> =>
    api
      .put(`/asesores/${id}/disponible?disponible=${disponible}`)
      .then((r) => r.data),
};
