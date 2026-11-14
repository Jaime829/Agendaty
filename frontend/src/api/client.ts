import axios, { AxiosError } from 'axios';
import { authService } from './auth';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    const userId = authService.getUser()?.usuarioId;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    if (userId) {
      config.headers['X_Usuario_Id'] = userId;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Mapea errores de la API a mensajes claros
export class ApiClientError extends Error {
  status: number;
  details: string;
  constructor(message: string, status: number, details: string) {
    super(message);
    this.status = status;
    this.details = details;
  }
}

api.interceptors.response.use(
  (res) => res,
  (err: AxiosError) => {
    if (err.response) {
      const { status, data } = err.response;
      const body = data as any;
      const message = body?.errorMessage || body?.message || `Error ${status}`;
      const details = body?.errorsDetails || '';
      throw new ApiClientError(message, status as number, details);
    }
    throw err;
  }
);

export default api;
