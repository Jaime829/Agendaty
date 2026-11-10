import api from './client';

export interface UsuarioDto {
  id: number;
  nombre: string;
  username: string;
  rol: string;
}

export interface AsignarRolParams {
  rol: string;
}

export const usuariosService = {
  listar: (): Promise<UsuarioDto[]> => api.get('/usuarios').then((r) => r.data),
  crear: (data: {
    nombre: string;
    username: string;
    password: string;
    email?: string;
    rol: string;
  }): Promise<UsuarioDto> => api.post('/usuarios', data).then((r) => r.data),
  asignarRol: (id: number, params: AsignarRolParams): Promise<UsuarioDto> =>
    api.put(`/usuarios/${id}/rol`, params).then((r) => r.data),
};
