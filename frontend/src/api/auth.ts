import api from './client';
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types';

export const authService = {
  login: (data: LoginRequest): Promise<LoginResponse> =>
    api.post('/auth/login', data).then((r) => r.data),
  register: (data: RegisterRequest): Promise<LoginResponse> =>
    api.post('/auth/register', data).then((r) => r.data),
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
