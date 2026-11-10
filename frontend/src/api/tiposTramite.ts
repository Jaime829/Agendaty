import api from './client';
import type { TipoTramite, TipoTramiteRequest } from '@/types';
import type { AxiosResponse } from 'axios';

export const tiposTramiteService = {
  listar: (): Promise<TipoTramite[]> =>
    api.get('/tipos-tramite').then((r) => r.data),
  obtener: (id: number): Promise<TipoTramite> =>
    api.get(`/tipos-tramite/${id}`).then((r) => r.data),
  crear: (data: TipoTramiteRequest): Promise<TipoTramite> =>
    api.post('/tipos-tramite', data).then((r) => r.data),
  actualizar: (id: number, data: TipoTramiteRequest): Promise<TipoTramite> =>
    api.put(`/tipos-tramite/${id}`, data).then((r) => r.data),
  eliminar: (id: number): Promise<void> =>
    api.delete(`/tipos-tramite/${id}`).then(() => undefined),
};
