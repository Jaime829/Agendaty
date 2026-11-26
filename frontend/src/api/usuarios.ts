import api from './client';

export interface UsuarioDto {
  id: number;
  nombre: string;
  correo: string;
  rol: string;
}

export interface AsignarRolParams {
  rol: string;
}

export const usuariosService = {
  listar: (): Promise<UsuarioDto[]> => api.get('/admin/usuarios').then((r) => r.data),
  crear: (data: {
    nombre: string;
    correo: string;
    password: string;
    documento: string;
    telefono: string;
    rol: string;
  }): Promise<UsuarioDto> => api.post('/admin/usuarios', data).then((r) => r.data),
  asignarRol: (id: number, params: AsignarRolParams): Promise<UsuarioDto> =>
    api.put(`/admin/usuarios/${id}/rol`, params).then((r) => r.data),
};
