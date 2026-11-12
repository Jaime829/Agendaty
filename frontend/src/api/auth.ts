import api from './client';
import type { RegisterRequest } from '@/types';

export const authService = {
  login: async (data: { correo: string; password: string }): Promise<any> => {
    // Login simple sin JWT: valida contra mock o backend real
    if (import.meta.env.VITE_USE_MOCK === 'true') {
      return mockLogin(data);
    }
    const res = await api.post('/auth/login', data);
    return res.data;
  },
  registrar: async (data: RegisterRequest): Promise<any> => {
    if (import.meta.env.VITE_USE_MOCK === 'true') {
      return mockRegistro(data);
    }
    const res = await api.post('/auth/registro', data);
    return res.data;
  },
  setToken: (token: string) => localStorage.setItem('token', token),
  getToken: () => localStorage.getItem('token'),
  removeToken: () => localStorage.removeItem('token'),
  getUser: () => {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  },
  setUser: (user: any) => localStorage.setItem('user', JSON.stringify(user)),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};

function mockLogin(data: { correo: string; password: string }) {
  // Usuarios de ejemplo para modo mock
  const usuarios: Record<string, any> = {
    'admin@turnos.local': { usuarioId: 1, username: 'admin', correo: 'admin@turnos.local', rol: 'ROLE_ADMINISTRADOR', password: 'Admin123*' },
    'recepcion@turnos.local': { usuarioId: 2, username: 'recepcion', correo: 'recepcion@turnos.local', rol: 'ROLE_RECEPCIONISTA', password: 'Rec123*' },
    'asesor@turnos.local': { usuarioId: 3, username: 'asesor1', correo: 'asesor@turnos.local', rol: 'ROLE_ASESOR', password: 'Ase123*' },
    'coord@turnos.local': { usuarioId: 4, username: 'coord', correo: 'coord@turnos.local', rol: 'ROLE_COORDINADOR', password: 'Cor123*' },
    'juan@email.com': { usuarioId: 5, username: 'juan', correo: 'juan@email.com', rol: 'ROLE_CIUDADANO', password: 'Cli123*' },
  };
  const u = usuarios[data.correo];
  if (!u || u.password !== data.password) {
    throw new Error('Credenciales inválidas');
  }
  const { password, ...sinPassword } = u;
  localStorage.setItem('token', 'demo-token');
  localStorage.setItem('user', JSON.stringify(sinPassword));
  return { token: 'demo-token', ...sinPassword };
}

function mockRegistro(data: RegisterRequest) {
  // En modo mock, el registro siempre crea un ciudadano
  const user = {
    usuarioId: Date.now(),
    username: data.correo.split('@')[0],
    correo: data.correo,
    rol: 'ROLE_CIUDADANO',
    documento: data.documento,
    telefono: data.telefono,
  };
  localStorage.setItem('token', 'demo-token');
  localStorage.setItem('user', JSON.stringify(user));
  return { token: 'demo-token', ...user };
}
