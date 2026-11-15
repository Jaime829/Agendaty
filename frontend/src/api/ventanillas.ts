import api from './client';

export interface VentanillaDto {
  id: number;
  numero: number;
  descripcion: string;
  asesorId?: number | null;
}

export interface VentanillaRequest {
  numero: number;
  descripcion: string;
}

export const ventanillasService = {
  listar: (): Promise<VentanillaDto[]> => api.get('/ventanillas').then((r) => r.data),
  crear: (data: VentanillaRequest): Promise<VentanillaDto> => api.post('/ventanillas', data).then((r) => r.data),
  actualizar: (id: number, data: VentanillaRequest): Promise<VentanillaDto> =>
    api.put(`/ventanillas/${id}`, data).then((r) => r.data),
  eliminar: (id: number): Promise<void> => api.delete(`/ventanillas/${id}`).then(() => undefined),
};
