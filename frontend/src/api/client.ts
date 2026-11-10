import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

export const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Inyecta el token JWT en cada petición
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

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
      const message =
        body?.errorMessage || body?.message || `Error ${status}`;
      const details = body?.errorsDetails || '';
      throw new ApiClientError(message, status, details);
    }
    throw err;
  }
);

export default api;
